package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.Product.ProductResponse;
import com.appshop.back_shop.service.FavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorite")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteController {
    FavoriteService favoriteService;

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
