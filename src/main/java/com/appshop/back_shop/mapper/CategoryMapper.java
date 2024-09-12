package com.appshop.back_shop.mapper;


import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.dto.request.categories.CategoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);

    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}
