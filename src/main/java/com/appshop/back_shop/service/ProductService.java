package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.request.categories.CategoryRequest;
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

    @PreAuthorize("hasRole('ADMIN')")
    public ProductStockResponse updateStock(Long productId, ProductStockRequest request){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        int oldStock = product.getStock();
        int newStock = oldStock + request.getQuantity();

        if (newStock < 0)
            throw new AppException(ErrorCode.STOCK_NOT_VALID);

        product.setStock(newStock);

        productRepository.save(product);

        return ProductStockResponse.builder()
                .productId(product.getProductId())
                .oldStock(oldStock)
                .newStock(newStock)
                .build();
    }


    // fetch cac san pham co stock sap het hang
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductLowStockResponse> fetchAllLowProducts(int threshold){
        List<Product> products = productRepository.findByStockLessThan(threshold);
        return products.stream().map(product ->
                ProductLowStockResponse.builder()
                        .productId(product.getProductId())
                        .productName(product.getName())
                        .stock(product.getStock())
                        .build()).collect(Collectors.toList());
    }

    public List<ProductWithCategoryResponse> fetchProductsWithCategories(Long categoryId) {
        List<Product> products = productRepository.findByCategory_CategoryId(categoryId);
        return products.stream()
                .map(product -> ProductWithCategoryResponse.builder()
                        .productId(product.getProductId())
                        .productName(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .size(product.getSize())
                        .color(product.getColor())
                        .imgProduct(product.getImgProduct())
                        .categoryName(product.getCategory().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ProductResponse> fetchProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice){
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
