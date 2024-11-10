package com.appshop.back_shop.dto.request.checkout;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private List<Long> cartItemIds;
}
