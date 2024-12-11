package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String productName);

    Optional<Product> findByProductId(Long id);

    List<Product> findByCategoryAndIsDeletedFalse(Category category);

    List<Product> findByIsDeletedFalse();

    Page<Product> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    List<Product> findByIsDeletedFalse(Sort sort);

    List<Product> findByNameContainingAndIsDeletedFalse(String name, Sort sort);

    List<Product> findByPriceBetweenAndIsDeletedFalse(Double priceMin, Double priceMax, Sort sort);

    List<Product> findByNameContainingAndPriceBetweenAndIsDeletedFalse(String name, Double priceMin, Double priceMax, Sort sort);

}
