package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByPoolCodeAndActiveTrue(String poolCode);

    Optional<Coupon> findByCode(String code);

    List<Coupon> findByActiveTrueAndExpiryDateAfter(LocalDateTime now);

    @Query("SELECT c FROM Coupon c " +
            "JOIN UserCoupon uc ON uc.coupon.id = c.id " +
            "WHERE uc.user.id = :userId AND c.active = true AND c.expiryDate > CURRENT_TIMESTAMP")
    List<Coupon> findActiveCouponsByUser(@Param("userId") Long userId);
}
