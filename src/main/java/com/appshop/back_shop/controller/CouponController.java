package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Coupon;
import com.appshop.back_shop.dto.request.coupon.CouponRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.checkout.ApplyCouponWithProductsResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/list-all")
    public List<Coupon> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @PostMapping("/apply-coupon")
    public ApiResponse<ApplyCouponWithProductsResponse> applyCoupon(@RequestBody CouponRequest couponRequest) {
        return ApiResponse.<ApplyCouponWithProductsResponse>builder().code(200).result(couponService.applyCoupon(couponRequest.getPoolCode())).build();
    }

    @PutMapping("/{couponId}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long couponId, @RequestBody @Valid Coupon updatedCoupon) {
        try {
            Coupon updated = couponService.updateCoupon(couponId, updatedCoupon);
            return ResponseEntity.ok(updated);
        } catch (AppException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Long couponId) {
        try {
            couponService.deleteCoupon(couponId);
            return ResponseEntity.ok("Coupon deactivated successfully.");
        } catch (AppException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deactivating coupon: " + ex.getMessage());
        }
    }

}
