package com.appshop.back_shop.repository;


import com.appshop.back_shop.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    Optional<Order> findByOrderId(Long orderId);

    List<Order> findByUser_IdAndStatus(Long userId, String status);

    List<Order> findByUser_IdAndStatusNot(Long userId, String status);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findByStatus(String status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE (:status = 'all' OR o.status = :status) " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "AND (:fullName IS NULL OR o.shippingAddress.fullName LIKE %:fullName%) " +
            "AND (:phoneNumber IS NULL OR o.shippingAddress.phoneNumber LIKE %:phoneNumber%) " +
            "AND (:addressDetail IS NULL OR o.shippingAddress.addressDetail LIKE %:addressDetail%) " +
            "ORDER BY o.createdAt DESC")
    Page<Order> searchOrders(@Param("status") String status,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("fullName") String fullName,
                             @Param("phoneNumber") String phoneNumber,
                             @Param("addressDetail") String addressDetail,
                             Pageable pageable);


}
