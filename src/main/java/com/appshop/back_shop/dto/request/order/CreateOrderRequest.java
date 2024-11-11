package com.appshop.back_shop.dto.request.order;

import com.appshop.back_shop.dto.request.cart.CartItemRequest;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private List<CartItemRequest> cartItems;  // List of cart items that will be converted to order items
    private String couponCode;  // Optional coupon code to apply discounts
}
