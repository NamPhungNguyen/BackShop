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
    int stock;
    String size;
    String color;
    List<String> imgProduct;
    Long categoryId;
    String categoryName;
    LocalDateTime createdAt;
    LocalDateTime updateAt;
}
