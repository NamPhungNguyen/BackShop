package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.domain.Order;
import com.appshop.back_shop.domain.OrderItem;
import com.appshop.back_shop.dto.response.order.OrderResponse;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    ShippingAddressRepository shippingAddressRepository;
    AddressService addressService;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    @Transactional
    public OrderResponse createOrder(String paymentMethod) {
        try {
            Long userId = getUserIdFromToken();
            List<CartItem> selectedItems = cartItemRepository.findByCart_User_IdAndCheckedOutTrue(userId);

            if (selectedItems.isEmpty()) {
                throw new RuntimeException("No items found in cart for checkout.");
            }

            BigDecimal totalBeforeDiscount = BigDecimal.ZERO;
            for (CartItem cartItem : selectedItems) {
                BigDecimal discountPrice = cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                totalBeforeDiscount = totalBeforeDiscount.add(totalPrice);
            }

            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal totalAfterDiscount = totalBeforeDiscount.subtract(discountAmount);

            Order order = Order.builder()
                    .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                    .status("pending")
                    .totalAmount(totalAfterDiscount)
                    .discountAmount(discountAmount)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Order savedOrder = orderRepository.save(order);

            selectedItems.forEach(cartItem -> {
                BigDecimal discountPrice = cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                BigDecimal price = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(price)
                        .build();

                orderItemRepository.save(orderItem);
            });

            return new OrderResponse(savedOrder.getOrderId(), totalAfterDiscount, paymentMethod, selectedItems);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }



}
