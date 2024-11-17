package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String productName);

    Optional<Product> findByProductId(Long id);

    List<Product> findByCategory(Category category);

    Page<Product> findAll(Pageable pageable);

    List<Product> findByNameContaining(String name);

    List<Product> findByPriceBetween(Double priceMin, Double priceMax);

}
