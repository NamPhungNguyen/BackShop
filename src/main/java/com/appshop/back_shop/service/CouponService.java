package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.domain.Coupon;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.domain.UserCoupon;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.checkout.ApplyCouponWithProductsResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.CartItemRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponService {
    CouponRepository couponRepository;
    UserRepository userRepository;
    UserCouponRepository userCouponRepository;
    CartItemRepository cartItemRepository;

    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expiry date must be in the future.");
        }

        if (coupon.getTotalQuantity() <= 0) {
            throw new RuntimeException("Total quantity must be greater than zero.");
        }

        coupon.setRemainingQuantity(coupon.getTotalQuantity());

        coupon.setActive(true);

        return couponRepository.save(coupon);
    }

    public List<Coupon> getActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByActiveTrueAndExpiryDateAfter(now);
    }

    @Transactional
    public UserCoupon claimCoupon(String poolCode) {
        Coupon coupon = couponRepository.findByPoolCodeAndActiveTrue(poolCode).orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        if (coupon.getRemainingQuantity() <= 0) {
            throw new AppException(ErrorCode.COUPON_OUT_OF_STOCK);
        }

        boolean hasUserClaimed = userCouponRepository.existsByUserIdAndCoupon_PoolCodeAndIsUsedFalse(getUserIdFromToken(), poolCode);
        if (hasUserClaimed) {
            throw new AppException(ErrorCode.COUPON_ALREADY_CLAIMED);
        }

        User user = userRepository.findById(getUserIdFromToken()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

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


    public ApplyCouponWithProductsResponse applyCoupon(String couponCode) {
        Long userId = getUserIdFromToken();

        List<CartItem> selectedItems = cartItemRepository.findByCart_User_IdAndCheckedOutTrue(userId);

        AtomicReference<BigDecimal> totalBeforeDiscount = new AtomicReference<>(BigDecimal.ZERO);

        List<CartItemResponse> selectedItemResponses = selectedItems.stream().map(cartItem -> {
            BigDecimal discountPrice = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(1 - cartItem.getProduct().getDiscount().doubleValue() / 100));
            BigDecimal totalPrice = discountPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            // Accumulate total price for all selected items
            totalBeforeDiscount.updateAndGet(v -> v.add(totalPrice));

            return new CartItemResponse(
                    cartItem.getCartItemId(),
                    cartItem.getProduct().getProductId(),
                    cartItem.getProduct().getName(),
                    !cartItem.getProduct().getImgProduct().isEmpty() ? cartItem.getProduct().getImgProduct().get(0) : null,
                    cartItem.getProduct().getPrice(),
                    cartItem.getSize(),
                    cartItem.getColor(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getDiscount(),
                    discountPrice,
                    totalPrice,
                    cartItem.isCheckedOut()
            );
        }).collect(Collectors.toList());

        Coupon coupon = couponRepository.findByCode(couponCode)
                .filter(c -> c.isActive() && LocalDateTime.now().isBefore(c.getExpiryDate()) && c.getRemainingQuantity() > 0)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_VALID));
        BigDecimal discountAmount = BigDecimal.valueOf(coupon.getDiscountAmount());
        BigDecimal totalAfterDiscount = totalBeforeDiscount.get().subtract(discountAmount);

        return new ApplyCouponWithProductsResponse(totalAfterDiscount, discountAmount, totalBeforeDiscount.get(), selectedItemResponses);
    }
}
