package com.appshop.back_shop.dto.response.checkout;

import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplyCouponWithProductsResponse  {
    BigDecimal totalAfterDiscount;
    BigDecimal discountAmount;
    BigDecimal totalBeforeDiscount;
    List<CartItemResponse> selectedItems;
}
