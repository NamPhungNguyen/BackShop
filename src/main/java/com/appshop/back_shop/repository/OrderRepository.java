package com.appshop.back_shop.repository;


import com.appshop.back_shop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Optional<Order> findByOrderId(Long orderId);
    List<Order> findByUser_IdAndStatus(Long userId, String status);
    List<Order> findByUser_IdAndStatusNot(Long userId, String status);
}
