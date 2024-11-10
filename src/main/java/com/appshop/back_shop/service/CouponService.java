package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Coupon;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.domain.UserCoupon;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.CouponRepository;
import com.appshop.back_shop.repository.UserCouponRepository;
import com.appshop.back_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponService {
    CouponRepository couponRepository;
    UserRepository userRepository;
    UserCouponRepository userCouponRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        // Kiểm tra ngày hết hạn của coupon có hợp lệ
        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expiry date must be in the future.");
        }

        // Kiểm tra số lượng mã có hợp lệ không
        if (coupon.getTotalQuantity() <= 0) {
            throw new RuntimeException("Total quantity must be greater than zero.");
        }

        // Thiết lập số lượng còn lại ban đầu bằng với tổng số lượng
        coupon.setRemainingQuantity(coupon.getTotalQuantity());

        // Kích hoạt coupon và lưu coupon vào cơ sở dữ liệu
        coupon.setActive(true);

        return couponRepository.save(coupon);
    }

    @Transactional
    public UserCoupon claimCoupon(String poolCode) {
        Coupon coupon = couponRepository.findByPoolCodeAndActiveTrue(poolCode)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.getRemainingQuantity() <= 0) {
            throw new AppException(ErrorCode.COUPON_OUT_OF_STOCK);
        }

        boolean hasUserClaimed = userCouponRepository.existsByUserIdAndCoupon_PoolCodeAndIsUsedFalse(getUserIdFromToken(), poolCode);
        if (hasUserClaimed) {
            throw new AppException(ErrorCode.COUPON_ALREADY_CLAIMED);
        }

        User user = userRepository.findById(getUserIdFromToken())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setClaimedAt(LocalDate.now());
        userCoupon.setUsed(false);

        coupon.setRemainingQuantity(coupon.getRemainingQuantity() - 1);
        userCouponRepository.save(userCoupon);
        couponRepository.save(coupon);

        return userCoupon;
    }

}
