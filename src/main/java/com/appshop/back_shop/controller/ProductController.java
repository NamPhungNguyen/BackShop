package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.request.product.ProductRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "Product")
public class ProductController {
    ProductService productService;

    @PostMapping("/create")
    ApiResponse<Product> createProduct(@RequestBody ProductRequest request){
        return ApiResponse.<Product>builder()
                .result(productService.createProduct(request))
                .code(200)
                .message("Create product successfully")
                .build();
    }
}
