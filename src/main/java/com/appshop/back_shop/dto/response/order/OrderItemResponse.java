package com.appshop.back_shop.dto.response.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private int quantity;
    private BigDecimal price;
}
