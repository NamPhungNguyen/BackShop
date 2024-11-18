package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Coupon;
import com.appshop.back_shop.domain.UserCoupon;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.checkout.ApplyCouponWithProductsResponse;
import com.appshop.back_shop.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/coupon")
@Tag(name = "Coupon")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponController {
    CouponService couponService;

    @PostMapping
    ApiResponse<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ApiResponse.<Coupon>builder().code(200).result(createdCoupon).build();
    }

    @GetMapping("/active")
    List<Coupon> getActiveCoupons() {
        return couponService.getActiveCoupons();
    }


    @PostMapping("/apply-coupon")
    ApiResponse<ApplyCouponWithProductsResponse> applyCoupon(@RequestParam String poolCode) {
        return ApiResponse.<ApplyCouponWithProductsResponse>builder().code(200).result(couponService.applyCoupon(poolCode)).build();
    }
}
