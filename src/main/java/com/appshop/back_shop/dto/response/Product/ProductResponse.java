package com.appshop.back_shop.dto.response.Product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long productId;
    String name;
    String description;
    BigDecimal price;
    BigDecimal discount;
    int stock;
    List<String> size;
    List<String> color;
    boolean isAvailable;
    double rating;
    int ratingCount;
    String brand;
    String productCode;
    List<String> imgProduct;
    Long categoryId;
    String categoryName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
