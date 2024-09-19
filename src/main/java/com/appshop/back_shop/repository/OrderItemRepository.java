package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Order;
import com.appshop.back_shop.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    Optional<OrderItem> deleteByOrder(Order order);
}
