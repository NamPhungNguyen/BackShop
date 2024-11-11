package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.domain.Order;
import com.appshop.back_shop.domain.OrderItem;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.order.CreateOrderRequest;
import com.appshop.back_shop.dto.response.order.OrderResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.CartItemRepository;
import com.appshop.back_shop.repository.OrderItemRepository;
import com.appshop.back_shop.repository.OrderRepository;
import com.appshop.back_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Get the user ID from the JWT token
    private Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (Long) authenticationToken.getPrincipal();
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Long userId = getUserIdFromToken();

        // Get user's cart items (Only items marked as checked out)
        List<CartItem> cartItems = cartItemRepository.findByCart_User_IdAndCheckedOutTrue(userId);

        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_CART);
        }

        // Calculate the total amount before any discounts
        BigDecimal totalAmount = cartItems.stream()
                .map(cartItem -> {
                    BigDecimal discountPrice = cartItem.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
                    return discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Retrieve the user
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Create the order object
        Order order = Order.builder()
                .user(user)
                .status("pending") // Status could be "pending", you might change this based on your workflow
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save the order first and assign the saved order to a variable
        Order savedOrder = orderRepository.save(order);

        // Create order items for each cart item
        cartItems.forEach(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder) // Use the saved order here
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        });

        // Optionally, mark the cart items as checked out
        cartItems.forEach(cartItem -> cartItem.setCheckedOut(true));
        cartItemRepository.saveAll(cartItems);

        // Return a response with the order details
        return new OrderResponse(savedOrder.getOrderId(), totalAmount, savedOrder.getStatus());
    }
}
