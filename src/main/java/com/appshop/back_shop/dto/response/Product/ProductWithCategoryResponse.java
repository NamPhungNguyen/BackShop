package com.appshop.back_shop.dto.response.Product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductWithCategoryResponse {
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private int stock;
    private String size;
    private String color;
    private List<String> imgProduct;
    private String categoryName;
}
