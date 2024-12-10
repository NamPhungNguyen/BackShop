package com.appshop.back_shop.dto.request.email;

import lombok.Data;

@Data
public class OTPValidationRequest {
    private String email;
    private String otp;
}
