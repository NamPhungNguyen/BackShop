package com.appshop.back_shop.dto.request.order;

import lombok.Data;

@Data
public class OrderRequest {
    private String paymentMethod;
    private String couponCode;
    private Long addressId;
}
