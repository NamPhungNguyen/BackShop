package com.appshop.back_shop.dto.request.cart;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemRequest {
    Long productId;
    int quantity;
}
