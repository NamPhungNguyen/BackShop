package com.appshop.back_shop.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OrderListResponse {
    private Long orderId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private List<OrderItemDetailResponse> products;
    private Long addressId;
}
