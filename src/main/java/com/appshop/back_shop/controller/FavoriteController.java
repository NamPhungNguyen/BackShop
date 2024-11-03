package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Product;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import com.appshop.back_shop.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add/{productId}")
    public ApiResponse<Void> addFavorite(@PathVariable Long productId) {
        favoriteService.addFavorite(productId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Success")
                .build();
    }

    @DeleteMapping("/remove/{productId}")
    public void removeFavorite(@PathVariable Long productId) {
        favoriteService.removeFavorite(productId);
    }

    @GetMapping()
    public List<ProductResponse> getFavoriteProducts() {
        return favoriteService.getFavoriteProducts();
    }
}
