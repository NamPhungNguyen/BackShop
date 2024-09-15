package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.request.categories.CategoryRequest;
import com.appshop.back_shop.dto.request.product.ProductRequest;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.mapper.ProductMapper;
import com.appshop.back_shop.repository.CategoryRepository;
import com.appshop.back_shop.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(ProductRequest request){
        if (productRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.PRODUCT_EXISTED);

        Category category = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Product product = productMapper.toProduct(request);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        ProductResponse response = productMapper.toProductResponse(savedProduct);

        return response;
    }

    public List<ProductResponse> getListProduct(){
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse fetchProductId(Long id) {
        Product product = productRepository.findByProductId(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return productMapper.toProductResponse(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse fetchProductName(String name){
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return productMapper.toProductResponse(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProductId(Long id, ProductRequest request) {
        Product product = productRepository.findByProductId(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        if (productRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.PRODUCT_EXISTED);

        Category category = categoryRepository.findByCategoryId(request.getCategoryId())
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSize(request.getSize());
        product.setColor(request.getColor());
        product.setImgProduct(request.getImgProduct());
        product.setCategory(category);

        Product updateProduct = productRepository.save(product);
        return productMapper.toProductResponse(updateProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }
}
