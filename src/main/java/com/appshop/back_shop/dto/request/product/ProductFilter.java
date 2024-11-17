package com.appshop.back_shop.dto.request.product;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductFilter {
    private String name;
    private Double priceMin;
    private Double priceMax;
}
