package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.*;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.order.OrderCancelResponse;
import com.appshop.back_shop.dto.response.order.OrderResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    CouponRepository couponRepository;
    ShippingAddressRepository shippingAddressRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    @Transactional
    public OrderResponse createOrder(String paymentMethod, String couponCode, Long addressId) {
        try {
            Long userId = getUserIdFromToken();

            ShippingAddress shippingAddress = shippingAddressRepository.findById(addressId)
                    .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_ADDRESS_NOT_FOUND));

            // Fetch and check cart items for the user, only the selected (checked-out) ones
            List<CartItem> selectedItems = cartItemRepository.findByCart_User_IdAndCheckedOutTrue(userId);
            if (selectedItems.isEmpty()) {
                throw new RuntimeException("No items found in cart for checkout.");
            }

            // Calculate the total amount before discount and each item's discounted price
            BigDecimal totalBeforeDiscount = selectedItems.stream().map(cartItem -> {
                BigDecimal discountPrice = cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                return discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            // Initialize discountAmount as zero if no coupon code is provided
            BigDecimal discountAmount = BigDecimal.ZERO;

            // Apply coupon code if provided
            if (couponCode != null && !couponCode.isEmpty()) {
                discountAmount = applyDiscountCode(couponCode, totalBeforeDiscount);
            }

            // Calculate total after discount
            BigDecimal totalAfterDiscount = totalBeforeDiscount.subtract(discountAmount);

            // Build and save the order
            Order order = Order.builder()
                    .user(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)))
                    .shippingAddress(shippingAddress)
                    .status("pending")
                    .totalAmount(totalAfterDiscount)
                    .discountAmount(discountAmount)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Order savedOrder = orderRepository.save(order);

            // Convert CartItem to CartItemResponse for the OrderResponse
            List<CartItemResponse> cartItemResponses = selectedItems.stream().map(cartItem -> {
                BigDecimal discountPrice = cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                return new CartItemResponse(
                        cartItem.getCartItemId(),
                        cartItem.getProduct().getProductId(),
                        cartItem.getProduct().getName(),
                        !cartItem.getProduct().getImgProduct().isEmpty() ? cartItem.getProduct().getImgProduct().get(0) : null,
                        cartItem.getProduct().getPrice(),
                        cartItem.getSize(),
                        cartItem.getColor(),
                        cartItem.getQuantity(),
                        cartItem.getProduct().getDiscount(),
                        discountPrice,
                        totalPrice,
                        cartItem.isCheckedOut()
                );
            }).collect(Collectors.toList());

            // Create and save order items from selected cart items
            selectedItems.forEach(cartItem -> {
                BigDecimal discountPrice = cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                BigDecimal price = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                // Create order item
                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(price)
                        .build();

                orderItemRepository.save(orderItem);

                // Now set `checkedOut` to false for selected cart items and remove them from the cart
                cartItem.setCheckedOut(false);  // Set `checkedOut` to false
                cartItemRepository.save(cartItem);  // Persist the change

                // Remove the item from the cart after creating the order item
                cartItemRepository.delete(cartItem); // Delete cart item after order creation
            });

            // Return an OrderResponse DTO with the cart items as CartItemResponse
            return new OrderResponse(savedOrder.getOrderId(), totalAfterDiscount, paymentMethod, cartItemResponses, addressId);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }


    @Transactional
    public OrderCancelResponse cancelOrder(Long orderId) {
        try {
            Long userId = getUserIdFromToken();

            Order order = orderRepository.findById(orderId).filter(o -> o.getUser().getId().equals(userId) && !o.getStatus().equals("cancelled")).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            order.getShippingAddress().getAddressId();

            if ("completed".equals(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_ALREADY_COMPLETED);
            }

            order.setStatus("cancelled");
            order.setUpdatedAt(LocalDateTime.now());

            orderRepository.save(order);

            return new OrderCancelResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), order.getShippingAddress(), order.getCreatedAt());

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Error cancelling order: " + e.getMessage());
        }
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        try {
            Long userId = getUserIdFromToken();

            // Fetch the order by ID and validate if the user is the owner of the order
            Order order = orderRepository.findById(orderId)
                    .filter(o -> o.getUser().getId().equals(userId))
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            // Ensure the new status is valid
            List<String> validStatuses = List.of("pending", "processing", "shipped", "completed", "cancelled");
            if (!validStatuses.contains(newStatus)) {
                throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
            }

            // Do not allow status update if the order is already completed or cancelled
            if ("completed".equals(order.getStatus()) || "cancelled".equals(order.getStatus())) {
                throw new AppException(ErrorCode.ORDER_ALREADY_COMPLETED);
            }

            // Update the order status and save
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            Order updatedOrder = orderRepository.save(order);

            // Fetch the order items associated with the updated order
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            List<CartItemResponse> cartItemResponses = mapOrderItemsToCartResponses(orderItems);

            // Return an OrderResponse with updated status and other details
            return new OrderResponse(
                    updatedOrder.getOrderId(),
                    updatedOrder.getTotalAmount(),
                    updatedOrder.getStatus(),
                    cartItemResponses,
                    updatedOrder.getShippingAddress().getAddressId()
            );

        } catch (AppException e) {
            // Rethrow known exceptions
            throw e;
        } catch (Exception e) {
            // Rollback transaction on any exception
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Error updating order status: " + e.getMessage());
        }
    }

    @Transactional
    public List<OrderResponse> getCompletedOrdersByUser() {
        try {
            Long userId = getUserIdFromToken(); // Lấy userId từ token

            // Tìm tất cả các đơn hàng của người dùng với trạng thái "completed"
            List<Order> completedOrders = orderRepository.findByUser_IdAndStatus(userId, "completed");

            if (completedOrders.isEmpty()) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND); // Nếu không có đơn hàng nào
            }

            // Chuyển đổi danh sách Order thành OrderResponse
            return completedOrders.stream()
                    .map(order -> {
                        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                        List<CartItemResponse> cartItemResponses = mapOrderItemsToCartResponses(orderItems);

                        return new OrderResponse(
                                order.getOrderId(),
                                order.getTotalAmount(),
                                order.getStatus(),
                                cartItemResponses,
                                order.getShippingAddress().getAddressId()
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving completed orders: " + e.getMessage());
        }
    }

    @Transactional
    public List<OrderResponse> getNotCompletedOrdersByUser() {
        try {
            // Tìm tất cả các đơn hàng chưa hoàn thành của người dùng
            List<Order> notCompletedOrders = orderRepository.findByUser_IdAndStatusNot(getUserIdFromToken(), "completed");

            if (notCompletedOrders.isEmpty()) {
                throw new AppException(ErrorCode.ORDER_NOT_FOUND); // Nếu không có đơn hàng nào
            }

            // Chuyển đổi danh sách Order thành OrderResponse
            return notCompletedOrders.stream()
                    .map(order -> {
                        // Lấy các OrderItems liên quan đến đơn hàng
                        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

                        // Chuyển đổi các OrderItems thành CartItemResponse
                        List<CartItemResponse> cartItemResponses = mapOrderItemsToCartResponses(orderItems);

                        // Tạo OrderResponse và trả về
                        return new OrderResponse(
                                order.getOrderId(),
                                order.getTotalAmount(),
                                order.getStatus(),
                                cartItemResponses,
                                order.getShippingAddress().getAddressId()
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving not completed orders: " + e.getMessage());
        }
    }


    private List<CartItemResponse> mapOrderItemsToCartResponses(List<OrderItem> orderItems) {
        return orderItems.stream().map(orderItem -> {
            Product product = orderItem.getProduct();
            return new CartItemResponse(
                    null,  // No CartItemId, since this is coming from the order
                    product.getProductId(),
                    product.getName(),
                    !product.getImgProduct().isEmpty() ? product.getImgProduct().get(0) : null,
                    product.getPrice(),
                    null,  // No size info in the order, adjust if needed
                    null,  // No color info in the order, adjust if needed
                    orderItem.getQuantity(),
                    product.getDiscount(),
                    orderItem.getPrice(),
                    orderItem.getPrice(),  // Assuming same for final price and discounted price
                    true  // Checked out = true, since it's an order
            );
        }).collect(Collectors.toList());
    }



    private BigDecimal applyDiscountCode(String couponCode, BigDecimal total) {
        Coupon coupon = couponRepository.findByCode(couponCode).filter(c -> c.isActive() && LocalDateTime.now().isBefore(c.getExpiryDate()) && c.getRemainingQuantity() > 0).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_VALID));

        // Calculate discount amount
        BigDecimal discountAmount = BigDecimal.valueOf(coupon.getDiscountAmount());

        // Optionally, update remaining quantity if needed
        coupon.setRemainingQuantity(coupon.getRemainingQuantity() - 1);
        couponRepository.save(coupon);

        return discountAmount.min(total);
    }
}
