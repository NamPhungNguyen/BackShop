package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.*;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.MonthlyRevenue;
import com.appshop.back_shop.dto.response.address.ShippingAddressResponse;
import com.appshop.back_shop.dto.response.order.OrderCancelResponse;
import com.appshop.back_shop.dto.response.order.OrderListAllResponse;
import com.appshop.back_shop.dto.response.order.OrderPageResponse;
import com.appshop.back_shop.dto.response.order.OrderResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    CouponRepository couponRepository;
    ShippingAddressRepository shippingAddressRepository;
    PaymentRepository paymentRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    @Transactional
    public OrderResponse createOrder(String couponCode, Long addressId, String paymentMethod) {
        try {
            Long userId = getUserIdFromToken();

            ShippingAddress shippingAddress = shippingAddressRepository.findById(addressId).orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

            List<CartItem> selectedItems = cartItemRepository.findByCart_User_IdAndCheckedOutTrue(userId);
            if (selectedItems.isEmpty()) {
                throw new RuntimeException("No items found in cart for checkout.");
            }

            BigDecimal totalBeforeDiscount = selectedItems.stream().map(cartItem -> {
                BigDecimal discountPrice = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                return discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal discountAmount = BigDecimal.ZERO;
            if (couponCode != null && !couponCode.isEmpty()) {
                discountAmount = applyDiscountCode(couponCode, totalBeforeDiscount);
            }

            BigDecimal totalAfterDiscount = totalBeforeDiscount.subtract(discountAmount);

            String resolvedPaymentMethod = (paymentMethod != null && paymentMethod.equalsIgnoreCase("transfer")) ? "transfer" : "COD";

            Order order = Order.builder().user(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))).shippingAddress(shippingAddress).status("pending").totalAmount(totalAfterDiscount).discountAmount(discountAmount).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

            Order savedOrder = orderRepository.save(order);

            selectedItems.forEach(cartItem -> {
                BigDecimal discountPrice = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                BigDecimal price = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                Product product = cartItem.getProduct();
                int quantityOrdered = cartItem.getQuantity();

                // Log stock before update
                System.out.println("Before update: Product - " + product.getName() + ", Stock - " + product.getStock());

                // Kiểm tra số lượng tồn kho
                if (product.getStock() < quantityOrdered) {
                    throw new RuntimeException("Not enough stock for product: " + product.getName());
                }

                // Trừ số lượng tồn kho
                product.setStock(product.getStock() - quantityOrdered);

                // Log stock after update
                System.out.println("After update: Product - " + product.getName() + ", Stock - " + product.getStock());

                // Lưu lại sản phẩm với số lượng tồn kho mới
                productRepository.save(product);

                OrderItem orderItem = OrderItem.builder().order(savedOrder).product(cartItem.getProduct()).quantity(cartItem.getQuantity()).price(price).size(cartItem.getSize()).color(cartItem.getColor()).build();
                orderItemRepository.save(orderItem);

                cartItem.setPurchased(true);
                cartItem.setCheckedOut(false);
                cartItemRepository.save(cartItem);
            });

            Payment payment = Payment.builder().order(savedOrder).amount(totalAfterDiscount).paymentMethod(resolvedPaymentMethod).paymentStatus(resolvedPaymentMethod.equals("transfer") ? "awaiting_transfer" : "pending").createdAt(LocalDateTime.now()).build();
            paymentRepository.save(payment);
            clearCartAfterOrder(userId);

            List<CartItemResponse> cartItemResponses = selectedItems.stream().map(cartItem -> {
                BigDecimal discountPrice = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                return new CartItemResponse(cartItem.getCartItemId(), cartItem.getProduct().getProductId(), cartItem.getProduct().getName(), !cartItem.getProduct().getImgProduct().isEmpty() ? cartItem.getProduct().getImgProduct().get(0) : null, cartItem.getProduct().getPrice(), cartItem.getSize(), cartItem.getColor(), cartItem.getQuantity(), cartItem.getProduct().getDiscount(), discountPrice, totalPrice, cartItem.isCheckedOut());
            }).collect(Collectors.toList());

            return new OrderResponse(savedOrder.getOrderId(), totalAfterDiscount, resolvedPaymentMethod, cartItemResponses, addressId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }

    @Transactional
    public Page<OrderPageResponse> getOrdersByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Order> ordersPage;

        if ("all".equalsIgnoreCase(status)) {
            ordersPage = orderRepository.findAll(pageable);
        } else {
            ordersPage = orderRepository.findByStatus(status, pageable);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<OrderPageResponse> orderResponses = ordersPage.getContent().stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

            List<CartItemResponse> cartItemResponses = orderItems.stream().map(orderItem -> {
                Product product = orderItem.getProduct();
                String sizes = orderItem.getSize();
                String color = orderItem.getColor();

                return new CartItemResponse(null, product.getProductId(), product.getName(), !product.getImgProduct().isEmpty() ? product.getImgProduct().get(0) : null, product.getPrice(), sizes, color, orderItem.getQuantity(), product.getDiscount(), orderItem.getPrice(), orderItem.getPrice(), true);
            }).collect(Collectors.toList());

            String createdAtFormatted = order.getCreatedAt().format(formatter);
            String updatedAtFormatted = order.getUpdatedAt().format(formatter);

            // Map ShippingAddress to ShippingAddressResponse
            ShippingAddressResponse shippingAddressResponse = null;
            Long addressId = null;

            if (order.getShippingAddress() != null) {
                ShippingAddress shippingAddress = order.getShippingAddress();
                addressId = shippingAddress.getAddressId();
                shippingAddressResponse = new ShippingAddressResponse(shippingAddress.getAddressId(), shippingAddress.getFullName(), shippingAddress.getPhoneNumber(), shippingAddress.getAddressDetail(), shippingAddress.getAdditionalAddress(), shippingAddress.getProvince(), shippingAddress.getCity(), shippingAddress.getCountry(), shippingAddress.getIsDefault());
            }

            return new OrderPageResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), cartItemResponses, addressId, shippingAddressResponse, createdAtFormatted, updatedAtFormatted);
        }).collect(Collectors.toList());

        return new PageImpl<>(orderResponses, pageable, ordersPage.getTotalElements());
    }

    @Transactional
    public Page<OrderPageResponse> searchOrders(String status, LocalDateTime startDate, LocalDateTime endDate, String fullName, String phoneNumber, String addressDetail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Order> ordersPage = orderRepository.searchOrders(status, startDate, endDate, fullName, phoneNumber, addressDetail, pageable);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<OrderPageResponse> orderResponses = ordersPage.getContent().stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

            List<CartItemResponse> cartItemResponses = orderItems.stream().map(orderItem -> {
                Product product = orderItem.getProduct();
                String sizes = orderItem.getSize();
                String color = orderItem.getColor();

                return new CartItemResponse(null, product.getProductId(), product.getName(), !product.getImgProduct().isEmpty() ? product.getImgProduct().get(0) : null, product.getPrice(), sizes, color, orderItem.getQuantity(), product.getDiscount(), orderItem.getPrice(), orderItem.getPrice(), true);
            }).collect(Collectors.toList());

            String createdAtFormatted = order.getCreatedAt().format(formatter);
            String updatedAtFormatted = order.getUpdatedAt().format(formatter);

            // Map ShippingAddress to ShippingAddressResponse
            ShippingAddressResponse shippingAddressResponse = null;
            Long addressId = null;

            if (order.getShippingAddress() != null) {
                ShippingAddress shippingAddress = order.getShippingAddress();
                addressId = shippingAddress.getAddressId();
                shippingAddressResponse = new ShippingAddressResponse(shippingAddress.getAddressId(), shippingAddress.getFullName(), shippingAddress.getPhoneNumber(), shippingAddress.getAddressDetail(), shippingAddress.getAdditionalAddress(), shippingAddress.getProvince(), shippingAddress.getCity(), shippingAddress.getCountry(), shippingAddress.getIsDefault());
            }

            return new OrderPageResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), cartItemResponses, addressId, // Add addressId to response
                    shippingAddressResponse, // Full shipping address object
                    createdAtFormatted, updatedAtFormatted);
        }).collect(Collectors.toList());

        return new PageImpl<>(orderResponses, pageable, ordersPage.getTotalElements());
    }

    private void clearCartAfterOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        cartItems.forEach(cartItem -> {
            if (cartItem.isPurchased()) {
                cartItem.setCheckedOut(false);
                cartItemRepository.save(cartItem);
            }
        });

        List<CartItem> purchasedItems = cartItems.stream().filter(CartItem::isPurchased).collect(Collectors.toList());

        if (!purchasedItems.isEmpty()) {
            cartItemRepository.deleteAll(purchasedItems);
        }
    }

    public List<MonthlyRevenue> getMonthlyRevenue() {
        List<Object[]> results = orderRepository.calculateMonthlyRevenue();

        // Tạo danh sách với 12 tháng gần nhất
        List<MonthlyRevenue> monthlyRevenueList = new ArrayList<>();
        LocalDate now = LocalDate.now();

        Map<String, Double> revenueMap = new HashMap<>();
        for (Object[] result : results) {
            int month = (Integer) result[0];
            int year = (Integer) result[1];
            BigDecimal revenueBD = (BigDecimal) result[2];
            revenueMap.put(year + "-" + month, revenueBD.doubleValue());
        }

        for (int i = 0; i < 12; i++) {
            LocalDate monthYear = now.minusMonths(i);
            String key = monthYear.getYear() + "-" + monthYear.getMonthValue();
            double revenue = revenueMap.getOrDefault(key, 0.0);

            monthlyRevenueList.add(new MonthlyRevenue(monthYear.getMonthValue(), monthYear.getYear(), revenue));
        }

        Collections.reverse(monthlyRevenueList);

        return monthlyRevenueList;
    }

    @Transactional
    public OrderCancelResponse cancelOrder(Long orderId) {
        try {
            Long userId = getUserIdFromToken();

            Order order = orderRepository.findById(orderId).filter(o -> o.getUser().getId().equals(userId) && !o.getStatus().equals("cancelled")).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            // Nếu trạng thái đơn hàng là "completed" hoặc "shipped", không thể hủy đơn hàng
            if ("completed".equals(order.getStatus()) || "shipped".equals(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_ALREADY_COMPLETED_OR_SHIPPED);
            }

            // Cập nhật trạng thái đơn hàng thành "cancelled"
            order.setStatus("cancelled");
            order.setUpdatedAt(LocalDateTime.now());

            orderRepository.save(order);

            return new OrderCancelResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), order.getShippingAddress(), order.getCreatedAt());

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

    @Transactional
    public List<OrderListAllResponse> getAllOrdersByUser() {
        Long userId = getUserIdFromToken();
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream().sorted(Comparator.comparing(Order::getCreatedAt).reversed()).map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

            List<CartItemResponse> cartItemResponses = orderItems.stream().map(orderItem -> {
                Product product = orderItem.getProduct();

                return new CartItemResponse(null, product.getProductId(), product.getName(), !product.getImgProduct().isEmpty() ? product.getImgProduct().get(0) : null, product.getPrice(), orderItem.getSize(), orderItem.getColor(), orderItem.getQuantity(), product.getDiscount(), orderItem.getPrice(), orderItem.getPrice(), true);
            }).collect(Collectors.toList());

            ShippingAddress shippingAddress = order.getShippingAddress();
            ShippingAddressResponse shippingAddressResponse = ShippingAddressResponse.builder().addressId(shippingAddress.getAddressId()).fullName(shippingAddress.getFullName()).phoneNumber(shippingAddress.getPhoneNumber()).addressDetail(shippingAddress.getAddressDetail()).additionalAddress(shippingAddress.getAdditionalAddress()).province(shippingAddress.getProvince()).city(shippingAddress.getCity()).country(shippingAddress.getCountry()).isDefault(shippingAddress.getIsDefault()).build();

            return new OrderListAllResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), cartItemResponses, shippingAddress.getAddressId(), // Lấy ID của địa chỉ giao hàng
                    shippingAddressResponse);
        }).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        try {
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            List<String> validStatuses = List.of("pending", "processing", "shipped", "completed", "cancelled");
            if (!validStatuses.contains(newStatus)) {
                throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
            }

            if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_ALREADY_COMPLETED);
            }

            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            Order updatedOrder = orderRepository.save(order);

            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            List<CartItemResponse> cartItemResponses = mapOrderItemsToCartResponses(orderItems);

            return new OrderResponse(updatedOrder.getOrderId(), updatedOrder.getTotalAmount(), updatedOrder.getStatus(), cartItemResponses, updatedOrder.getShippingAddress().getAddressId());

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Error updating order status: " + e.getMessage());
        }
    }

    @Transactional
    public List<OrderResponse> getCompletedOrdersByUser() {
        try {
            Long userId = getUserIdFromToken();
            List<Order> completedOrders = orderRepository.findByUser_IdAndStatus(userId, "completed");

            if (completedOrders.isEmpty()) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            return completedOrders.stream().map(order -> {
                List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                List<CartItemResponse> cartItemResponses = mapOrderItemsToCartResponses(orderItems);

                return new OrderResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), cartItemResponses, order.getShippingAddress().getAddressId());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving completed orders: " + e.getMessage());
        }
    }

    @Transactional
    public List<OrderResponse> getNotCompletedOrdersByUser() {
        try {
            List<Order> notCompletedOrders = orderRepository.findByUser_IdAndStatusNot(getUserIdFromToken(), "completed");

            if (notCompletedOrders.isEmpty()) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);
            }

            return notCompletedOrders.stream().map(order -> {
                List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

                List<CartItemResponse> cartItemResponses = mapOrderItemsToCartResponses(orderItems);

                return new OrderResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), cartItemResponses, order.getShippingAddress().getAddressId());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving not completed orders: " + e.getMessage());
        }
    }

    private List<CartItemResponse> mapOrderItemsToCartResponses(List<OrderItem> orderItems) {
        return orderItems.stream().map(orderItem -> {
            Product product = orderItem.getProduct();
            return new CartItemResponse(null, product.getProductId(), product.getName(), !product.getImgProduct().isEmpty() ? product.getImgProduct().get(0) : null, product.getPrice(), null, null, orderItem.getQuantity(), product.getDiscount(), orderItem.getPrice(), orderItem.getPrice(), true);
        }).collect(Collectors.toList());
    }

    private BigDecimal applyDiscountCode(String couponCode, BigDecimal total) {
        Coupon coupon = couponRepository.findByCode(couponCode).filter(c -> c.isActive() && LocalDateTime.now().isBefore(c.getExpiryDate()) && c.getRemainingQuantity() > 0).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_VALID));
        BigDecimal discountAmount = BigDecimal.valueOf(coupon.getDiscountAmount());

        coupon.setRemainingQuantity(coupon.getRemainingQuantity() - 1);
        couponRepository.save(coupon);
        return discountAmount.min(total);
    }
}
