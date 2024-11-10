package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByPoolCodeAndActiveTrue(String poolCode);
}
