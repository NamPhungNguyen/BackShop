package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.dto.request.categories.CategoryRequest;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.mapper.CategoryMapper;
import com.appshop.back_shop.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public Category createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(request);
        return categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Category> getListCategories(){
        return categoryRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Category getCategoryById(Long id){
       return categoryRepository.findCategoryByCategoryId(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Category updateCategory(CategoryRequest request, Long id){
        Category category = categoryRepository.findCategoryByCategoryId(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        if (categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);

        categoryMapper.updateCategory(category, request);

        return categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }
}
