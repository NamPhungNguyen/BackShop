package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByUserId(Long userId);
    @Query("SELECT s FROM ShippingAddress s WHERE s.user.id = :userId AND s.isDefault = true")
    Optional<ShippingAddress> findDefaultAddressByUserId(@Param("userId") Long userId);

}
