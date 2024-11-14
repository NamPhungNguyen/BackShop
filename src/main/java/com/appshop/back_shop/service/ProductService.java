package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.request.product.ProductRequest;
import com.appshop.back_shop.dto.request.product.ProductStockRequest;
import com.appshop.back_shop.dto.response.Product.ProductLowStockResponse;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import com.appshop.back_shop.dto.response.Product.ProductStockResponse;
import com.appshop.back_shop.dto.response.Product.ProductWithCategoryResponse;
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

import java.math.BigDecimal;
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

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        Category category = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));


        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .size(request.getSize())
                .color(request.getColor())
                .brand(request.getBrand())
                .imgProduct(request.getImgProduct())
                .category(category)
                .build();


        Product savedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .productId(savedProduct.getProductId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .discount(savedProduct.getDiscount())
                .stock(savedProduct.getStock())
                .size(savedProduct.getSize())
                .color(savedProduct.getColor())
                .isAvailable(savedProduct.isAvailable())
                .rating(savedProduct.getRating())
                .ratingCount(savedProduct.getRatingCount())
                .brand(savedProduct.getBrand())
                .productCode(savedProduct.getProductCode())
                .imgProduct(savedProduct.getImgProduct())
                .categoryId(category.getCategoryId())
                .categoryName(category.getName())
                .createdAt(savedProduct.getCreatedAt())
                .updatedAt(savedProduct.getUpdatedAt())
                .build();
    }

    public List<ProductResponse> getListProduct() {
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .toList();
        products.forEach(p -> System.out.println("Product ID: " + p.getProductId() + ", Comment Count: " + p.getCommentCount()));
        return products;
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
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
