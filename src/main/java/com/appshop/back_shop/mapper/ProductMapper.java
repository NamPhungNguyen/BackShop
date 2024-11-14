package com.appshop.back_shop.mapper;

import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.request.product.ProductRequest;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import jakarta.persistence.ManyToOne;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toProductResponse(Product request);
}
