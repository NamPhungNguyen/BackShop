package com.appshop.back_shop.controller;

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
}
