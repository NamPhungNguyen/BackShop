package com.appshop.back_shop.dto.response.order;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponseDetail {
    Long orderItemId;
    OrderProductDetailResponse product;
    int quantity;
    BigDecimal price;
}
