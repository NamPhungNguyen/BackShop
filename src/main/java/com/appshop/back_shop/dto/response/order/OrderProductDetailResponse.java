package com.appshop.back_shop.dto.response.order;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProductDetailResponse {
    Long productId;
    String productName;
}
