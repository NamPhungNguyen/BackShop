package com.appshop.back_shop.dto.response.order;

import com.appshop.back_shop.domain.ShippingAddress;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderCancelResponse {
    private Long orderId;
    private BigDecimal totalAmount;
    private String status;
    private ShippingAddress shippingAddress;
    private LocalDateTime createdAt;
}
