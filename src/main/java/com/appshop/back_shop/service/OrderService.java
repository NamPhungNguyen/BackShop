package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.*;
import com.appshop.back_shop.dto.response.order.OrderItemResponse;
import com.appshop.back_shop.dto.response.order.OrderResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    @Transactional
    public OrderResponse createOrder() {
        Long userId = getUserIdFromToken();

        // Fetch cart for the user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTS));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        BigDecimal totalAmount = cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(cart.getUser())
                .status("pending")
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(savedOrder)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(cartItems);

        List<OrderItemResponse> orderItemResponses = orderItems.stream()
                .map(orderItem -> OrderItemResponse.builder()
                        .orderItemId(orderItem.getOrderItemId())
                        .productId(orderItem.getProduct().getProductId())
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(savedOrder.getOrderId())
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .createdAt(savedOrder.getCreatedAt())
                .updatedAt(savedOrder.getUpdatedAt())
                .items(orderItemResponses)
                .build();
    }

    @Transactional
    public List<OrderResponse> fetchOrdersByUserId() {
        Long userId = getUserIdFromToken();

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(order -> {
                    List<OrderItemResponse> orderItemResponses = orderItemRepository.findByOrder(order).stream()
                            .map(orderItem -> OrderItemResponse.builder()
                                    .orderItemId(orderItem.getOrderItemId())
                                    .productId(orderItem.getProduct().getProductId())
                                    .quantity(orderItem.getQuantity())
                                    .price(orderItem.getPrice())
                                    .build())
                            .collect(Collectors.toList());

                    return OrderResponse.builder()
                            .orderId(order.getOrderId())
                            .status(order.getStatus())
                            .totalAmount(order.getTotalAmount())
                            .createdAt(order.getCreatedAt())
                            .updatedAt(order.getUpdatedAt())
                            .items(orderItemResponses) // Set the items field here
                            .build();
                })
                .collect(Collectors.toList());
    }



}
