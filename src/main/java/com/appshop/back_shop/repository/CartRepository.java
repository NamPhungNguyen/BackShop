package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Cart;
import com.appshop.back_shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    boolean existsByUser(User user);

    void deleteByUser_Id(Long userId);

    Optional<Cart> findByUserId(Long userId);

}