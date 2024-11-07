package com.appshop.back_shop.dto.response.Cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long cartItemId;
    Long productId;
    String productName;
    String imageUrl;
    BigDecimal price;
    String size;
    String color;
    int quantity;
    BigDecimal discount;
    BigDecimal totalPrice;
    BigDecimal discountPrice;
}
