package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.product.ProductFilter;
import com.appshop.back_shop.dto.request.product.ProductRequest;
import com.appshop.back_shop.dto.request.product.ProductUpdateRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import com.appshop.back_shop.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "Product")
public class ProductController {
    ProductService productService;

    @PostMapping("/create")
    ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ApiResponse.<ProductResponse>builder().result(productService.createProduct(request)).code(200).message("Create product successfully").build();
    }

    @GetMapping("/list")
    ApiResponse<List<ProductResponse>> fetchAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder().result(productService.getListProduct()).code(200).message("Get list product successful").build();
    }

    @GetMapping("/list-product-category/{categoryId}")
    ApiResponse<List<ProductResponse>> fetchAllProductByCategory(@PathVariable("categoryId") Long categoryId) {
        return ApiResponse.<List<ProductResponse>>builder().result(productService.getListProductByCategory(categoryId)).code(200).build();
    }

    @PutMapping("/update/{productId}")
    ApiResponse<ProductResponse> updateProductId(@PathVariable("productId") Long id, @RequestBody ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder().result(productService.updateProductId(id, request)).code(200).message("Update product successful").build();
    }

    @GetMapping("/products")
    public Page<ProductResponse> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getPagedProducts(pageable);
    }


    @DeleteMapping("/delete/{productId}")
    ApiResponse<Void> deleteProduct(@PathVariable("productId") Long id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder().message("Product deleted successful").code(200).build();
    }

    @GetMapping("/search")
    public List<ProductResponse> searchAndFilterProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(defaultValue = "true") boolean sortByPriceAsc) {

        ProductFilter filter = new ProductFilter();
        filter.setName(name);
        filter.setPriceMin(priceMin);
        filter.setPriceMax(priceMax);

        return productService.searchAndFilterProducts(filter, sortByPriceAsc);
    }


}
