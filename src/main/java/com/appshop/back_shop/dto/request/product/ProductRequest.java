package com.appshop.back_shop.dto.request.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductRequest {
    String name;
    String description;
    BigDecimal price;
    int stock;
    List<String> size;  // Changed from size to sizes
    List<String> color;  // Ensure you're using a list for multiple colors
    String brand;
    List<String> imgProduct;
    Long categoryId;
}
