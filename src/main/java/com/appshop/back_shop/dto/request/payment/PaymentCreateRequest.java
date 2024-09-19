package com.appshop.back_shop.dto.request.payment;


import lombok.Data;

@Data
public class PaymentCreateRequest {
    private String paymentMethod;
}
