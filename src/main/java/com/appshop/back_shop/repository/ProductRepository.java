package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String productName);
    Optional<Product> findByProductId(Long id);
    Optional<Product> findByName(String name);

    List<Product> findByStockLessThan(int stockThreshold);
    List<Product> findByCategory_CategoryId(Long categoryId);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

}
