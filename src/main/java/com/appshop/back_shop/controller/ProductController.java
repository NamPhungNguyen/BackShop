package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.request.product.ProductRequest;
import com.appshop.back_shop.dto.request.product.ProductStockRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.Product.ProductLowStockResponse;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import com.appshop.back_shop.dto.response.Product.ProductStockResponse;
import com.appshop.back_shop.dto.response.Product.ProductWithCategoryResponse;
import com.appshop.back_shop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "Product")
public class ProductController {
    ProductService productService;

    @PostMapping("/create")
    ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .code(200)
                .message("Create product successfully")
                .build();
    }

    @GetMapping("/list")
    ApiResponse<List<ProductResponse>> fetchAllProducts(){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getListProduct())
                .code(200)
                .message("Get list product successful")
                .build();
    }

    @GetMapping("/get-by-id/{productId}")
    ApiResponse<ProductResponse> fetchProductById(@PathVariable("productId") Long id){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.fetchProductId(id))
                .code(200)
                .message("Get product by Id successful")
                .build();
    }

    @GetMapping("/name/{productName}")
    ApiResponse<ProductResponse> fetchProductByName(@PathVariable("productName") String name){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.fetchProductName(name))
                .code(200)
                .message("Get product by name succesful")
                .build();
    }

    @PutMapping("/update/{productId}")
    ApiResponse<ProductResponse> updateProductId(@PathVariable("productId") Long id, @RequestBody ProductRequest request){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProductId(id, request))
                .code(200)
                .message("Update product successful")
                .build();
    }

    @DeleteMapping("/delete/{productId}")
    ApiResponse<Void> deleteProduct(@PathVariable("productId") Long id){
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Product deleted successful")
                .code(200)
                .build();
    }

    @PutMapping("/{productId}/stock")
    public ApiResponse<ProductStockResponse> updateStock(@PathVariable Long productId, @RequestBody ProductStockRequest request){
        return ApiResponse.<ProductStockResponse>builder()
                .code(200)
                .message("Stock updated successfully")
                .result(productService.updateStock(productId, request))
                .build();
    }

    @GetMapping("/low-stock")
    public ApiResponse<List<ProductLowStockResponse>> fetchLowStockProducts(@RequestParam(defaultValue = "10") int threshold){
        return ApiResponse.<List<ProductLowStockResponse>>builder()
                .code(200)
                .message("Low stock products retrieved successfully")
                .result(productService.fetchAllLowProducts(threshold))
                .build();
    }

    @GetMapping("/with-categories")
    public List<ProductWithCategoryResponse> getProductsWithCategories(@RequestParam Long categoryId) {
        return productService.fetchProductsWithCategories(categoryId);
    }

    @GetMapping("/by-price-range")
    public ApiResponse<List<ProductResponse>> fetchProductsByPriceRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.fetchProductsByPriceRange(minPrice, maxPrice))
                .code(200)
                .message("Products fetched successfully by price range")
                .build();
    }

}
