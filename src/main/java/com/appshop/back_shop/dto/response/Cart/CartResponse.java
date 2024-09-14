package com.appshop.back_shop.dto.response.Cart;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long cartId;
    Long userId;
    LocalDateTime createdAt;
}
