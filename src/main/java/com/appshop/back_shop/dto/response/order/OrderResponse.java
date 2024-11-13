package com.appshop.back_shop.dto.response.order;

import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private List<CartItemResponse> products;
    private Long addressId;
}