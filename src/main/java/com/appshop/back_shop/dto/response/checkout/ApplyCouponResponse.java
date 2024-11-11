package com.appshop.back_shop.dto.response.checkout;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ApplyCouponResponse {
    private BigDecimal totalAfterDiscount;
    private BigDecimal discountAmount;
    private BigDecimal totalBeforeDiscount;
}

