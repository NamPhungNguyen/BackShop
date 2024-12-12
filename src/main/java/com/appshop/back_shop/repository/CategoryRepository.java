package com.appshop.back_shop.repository;

import com.appshop.back_shop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    List<Category> findByIsDeletedFalse();

    Optional<Category> findCategoryByCategoryId(Long categoryId);

    Optional<Category> findByCategoryId(Long categoryId);

    Optional<Category> findCategoryByName(String categoryName);
}
