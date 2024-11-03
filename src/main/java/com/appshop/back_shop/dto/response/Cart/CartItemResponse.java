package com.appshop.back_shop.dto.response.Cart;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long cartItemId;
    Long productId;
    int quantity;
    String size;
    String color;
}
