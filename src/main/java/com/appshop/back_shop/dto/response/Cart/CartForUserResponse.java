package com.appshop.back_shop.dto.response.Cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartForUserResponse {
    Long cartId;
    Long userId;
    LocalDateTime createAt;
    List<CartItemResponse> items;
}
