package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    boolean existsByUserIdAndCoupon_PoolCodeAndIsUsedFalse(Long userId, String poolCode);

    List<UserCoupon> findByUser_IdAndIsUsedFalse(Long userId);
}
