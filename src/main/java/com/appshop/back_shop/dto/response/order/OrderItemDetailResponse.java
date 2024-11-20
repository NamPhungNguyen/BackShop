package com.appshop.back_shop.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OrderItemDetailResponse {
    private Long productId;
    private String productName;
    private List<String> imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private List<String> sizes;   // List of sizes
    private List<String> colors;  // List of colors
}
