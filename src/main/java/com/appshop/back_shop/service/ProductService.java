package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.request.product.ProductFilter;
import com.appshop.back_shop.dto.request.product.ProductRequest;
import com.appshop.back_shop.dto.request.product.ProductUpdateRequest;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

        Category category = categoryRepository.findByCategoryId(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Product product = Product.builder().name(request.getName()).description(request.getDescription()).price(request.getPrice()).stock(request.getStock()).size(request.getSize()).color(request.getColor()).brand(request.getBrand()).imgProduct(request.getImgProduct()).category(category).discount(request.getDiscount()).isAvailable(true).build();

        Product savedProduct = productRepository.save(product);

        return ProductResponse.builder().productId(savedProduct.getProductId()).name(savedProduct.getName()).description(savedProduct.getDescription()).price(savedProduct.getPrice()).discount(savedProduct.getDiscount()).stock(savedProduct.getStock()).size(savedProduct.getSize()).color(savedProduct.getColor()).isAvailable(savedProduct.isAvailable()).rating(savedProduct.getRating()).ratingCount(savedProduct.getRatingCount()).brand(savedProduct.getBrand()).productCode(savedProduct.getProductCode()).imgProduct(savedProduct.getImgProduct()).categoryId(category.getCategoryId()).categoryName(category.getName()).createdAt(savedProduct.getCreatedAt()).updatedAt(savedProduct.getUpdatedAt()).build();
    }

    public List<ProductResponse> getListProduct() {
        List<ProductResponse> products = productRepository.findByIsDeletedFalse().stream().map(productMapper::toProductResponse).collect(Collectors.toList());
        products.forEach(p -> log.info("Product ID: " + p.getProductId() + ", Comment Count: " + p.getCommentCount()));
        return products;
    }

    public Page<ProductResponse> getPagedProducts(Pageable pageable) {
        return productRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(product -> new ProductResponse(product.getProductId(), product.getName(), product.getDescription(), product.getPrice(), product.getDiscount(), product.getStock(), product.getSize(), product.getColor(), product.isAvailable(), product.getRating(), product.getRatingCount(), product.getCommentCount(), product.getBrand(), product.getProductCode(), product.getImgProduct(), product.getCategory().getCategoryId(), product.getCategory().getName(), product.getCreatedAt(), product.getUpdatedAt()));
    }

    public List<ProductResponse> getListProductByCategory(Long categoryId) {
        // Check if the category exists
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Fetch products by category and filter out deleted ones
        List<Product> products = productRepository.findByCategoryAndIsDeletedFalse(category);

        // Map the products to ProductResponse
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getDiscount(),
                        product.getStock(),
                        product.getSize(),
                        product.getColor(),
                        product.isAvailable(),
                        product.getRating(),
                        product.getRatingCount(),
                        product.getCommentCount(),
                        product.getBrand(),
                        product.getProductCode(),
                        product.getImgProduct(),
                        product.getCategory().getCategoryId(),
                        product.getCategory().getName(),
                        product.getCreatedAt(),
                        product.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchAndFilterProducts(ProductFilter filter, boolean sortByPriceAsc) {
        // Define sorting order
        Sort sort = Sort.by(sortByPriceAsc ? Sort.Order.asc("price") : Sort.Order.desc("price"));

        // Case 1: No filter provided
        if (filter == null || (filter.getName() == null && filter.getPriceMin() == null && filter.getPriceMax() == null)) {
            return productRepository.findByIsDeletedFalse(sort)
                    .stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        }

        // Case 2: Name and price range filter
        if (filter.getName() != null && filter.getPriceMin() != null && filter.getPriceMax() != null) {
            return productRepository.findByNameContainingAndPriceBetweenAndIsDeletedFalse(
                            filter.getName(),
                            filter.getPriceMin(),
                            filter.getPriceMax(),
                            sort)
                    .stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        }

        // Case 3: Name filter only
        if (filter.getName() != null) {
            return productRepository.findByNameContainingAndIsDeletedFalse(filter.getName(), sort)
                    .stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        }

        // Case 4: Price range filter only
        if (filter.getPriceMin() != null && filter.getPriceMax() != null) {
            return productRepository.findByPriceBetweenAndIsDeletedFalse(
                            filter.getPriceMin(),
                            filter.getPriceMax(),
                            sort)
                    .stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        }

        // Default: Return all products sorted by price
        return productRepository.findByIsDeletedFalse(sort)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscount(),
                product.getStock(),
                product.getSize(),
                product.getColor(),
                product.isAvailable(),
                product.getRating(),
                product.getRatingCount(),
                product.getCommentCount(),
                product.getBrand(),
                product.getProductCode(),
                product.getImgProduct(),
                product.getCategory().getCategoryId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProductId(Long id, ProductUpdateRequest request) {

        Product product = productRepository.findByProductId(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));


        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (productRepository.existsByName(request.getName())) {
                throw new AppException(ErrorCode.PRODUCT_EXISTED);
            }
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getStock() > 0) {
            product.setStock(request.getStock());
        }

        if (request.getSize() != null) {
            product.setSize(request.getSize());
        }

        if (request.getColor() != null) {
            product.setColor(request.getColor());
        }

        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }

        if (request.getImgProduct() != null) {
            product.setImgProduct(request.getImgProduct());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findByCategoryId(request.getCategoryId()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
            product.setCategory(category);
        }

        if (request.getDiscount() != null) {
            product.setDiscount(request.getDiscount());
        }

        if (request.getIsAvailable() != null) {
            product.setAvailable(request.getIsAvailable());
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductResponse(updatedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        product.setDeleted(true);
        productRepository.save(product);
    }
}
