package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findCategoryByCategoryId(Long categoryId);
}
