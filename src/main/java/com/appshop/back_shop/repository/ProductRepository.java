package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String productName);
    Optional<Product> findByProductId(Long id);
    Optional<Product> findByName(String name);

}
