package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.CartItem;
import com.appshop.back_shop.dto.request.cart.CartItemRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.Cart.CartForUserResponse;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.Cart.CartItemUpdateRequest;
import com.appshop.back_shop.dto.response.Cart.CartResponse;
import com.appshop.back_shop.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("/cart")
@Tag(name = "Cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping("/create")
    ApiResponse<CartResponse> createCart(){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.createCart())
                .code(200)
                .message("Create cart for user successfully")
                .build();
    }

    @DeleteMapping("/delete/{cartId}")
    ApiResponse<Void> deleteCart(@PathVariable("cartId") Long cartId){
        cartService.deleteCart(cartId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Cart for user deleted successfully")
                .build();
    }

    @DeleteMapping("/delete-by-userId/{userId}")
    ApiResponse<Void> deleteCartByUserId(@PathVariable("userId") Long userId){
        cartService.deleteCartByUserId(userId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Cart for user deleted successfully")
                .build();
    }

    @PostMapping("/add-item-to-cart")
    ApiResponse<CartItemResponse> addItemToCart(@RequestBody CartItemRequest request){
        return ApiResponse.<CartItemResponse>builder()
                .result(cartService.addItemToCart(request))
                .code(200)
                .message("add item to cart successfully")
                .build();
    }

    @GetMapping("/fetch-cart")
    ApiResponse<CartForUserResponse> fetchCartForUser(){
        return ApiResponse.<CartForUserResponse>builder()
                .result(cartService.fetchCartForUser())
                .code(200)
                .message("Get cart for user successfully")
                .build();
    }

    @PutMapping("/update-cart-item/{productId}")
    ApiResponse<CartItemResponse> updateCartItem(@PathVariable Long productId, @RequestBody CartItemUpdateRequest request){
        return ApiResponse.<CartItemResponse>builder()
                .result(cartService.updateCartItem(productId, request))
                .code(200)
                .message("Cart updated successfully")
                .build();
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ApiResponse<Void> deleteItemFromCart(@PathVariable("cartId") Long cartId, @PathVariable("itemId") Long itemId){
        cartService.deleteItemFromCart(cartId, itemId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Item removed from cart")
                .build();
    }

    @DeleteMapping("/{cartId}/items")
    public ApiResponse<Void> clearAllItemsFromCart(@PathVariable("cartId") Long cartId) {
        cartService.clearAllItemsFromCart(cartId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("All items cleared from cart")
                .build();
    }



}
