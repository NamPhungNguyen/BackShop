package com.appshop.back_shop.dto.response.Product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductLowStockResponse {
    Long productId;
    String productName;
    int stock;
}
