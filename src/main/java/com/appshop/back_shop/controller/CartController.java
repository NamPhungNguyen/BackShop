package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.cart.CartItemRequest;
import com.appshop.back_shop.dto.request.checkout.CheckoutRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.Cart.CartItemResponse;
import com.appshop.back_shop.dto.response.Cart.CartItemUpdateRequest;
import com.appshop.back_shop.dto.response.Cart.CartResponse;
import com.appshop.back_shop.dto.response.checkout.CheckoutResponse;
import com.appshop.back_shop.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/cart")
@Tag(name = "Cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @GetMapping("/user/cart")
    ApiResponse<CartResponse> getCart() {
        return ApiResponse.<CartResponse>builder().result(cartService.getCartForUser()).code(200).message("Cart retrieved successfully").build();
    }

    @PostMapping("/create/{userId}")
    ApiResponse<CartResponse> createCart(@PathVariable("userId") Long userId) {
        return ApiResponse.<CartResponse>builder().result(cartService.createCart(userId)).code(200).message("Create cart for user successfully").build();
    }

    @DeleteMapping("/delete/{cartId}")
    ApiResponse<Void> deleteCart(@PathVariable("cartId") Long cartId) {
        cartService.deleteCart(cartId);
        return ApiResponse.<Void>builder().code(200).message("Cart for user deleted successfully").build();
    }

    @DeleteMapping("/delete-by-userId/{userId}")
    ApiResponse<Void> deleteCartByUserId(@PathVariable("userId") Long userId) {
        cartService.deleteCartByUserId(userId);
        return ApiResponse.<Void>builder().code(200).message("Success").build();
    }

    @PostMapping("/add-item-to-cart")
    ApiResponse<CartItemResponse> addItemToCart(@RequestBody CartItemRequest request) {
        return ApiResponse.<CartItemResponse>builder().result(cartService.addItemToCart(request)).code(200).message("Success").build();
    }

    @PutMapping("/update-item-quantity/{cartItemId}")
    ApiResponse<CartItemResponse> updateItemQuantityFromCart(@PathVariable("cartItemId") Long cartItemId, @RequestBody CartItemUpdateRequest request) {
        return ApiResponse.<CartItemResponse>builder().result(cartService.updateItemQuantity(cartItemId, request.getQuantity())).code(200).message("Success").build();
    }

    @GetMapping("/fetch-cart")
    ApiResponse<List<CartItemResponse>> fetchCartForUser() {
        return ApiResponse.<List<CartItemResponse>>builder().result(cartService.fetchCartForUser()).code(200).message("Success").build();
    }

    @PutMapping("/update-checkout-status")
    ApiResponse<Void> updateCheckoutStatus(@RequestBody CheckoutRequest request, @RequestParam boolean isSelect) {
        cartService.updateItemsForCheckout(request, isSelect);
        return ApiResponse.<Void>builder().code(200).message("Success").build();
    }

    @GetMapping("/product-checkout")
    CheckoutResponse getProductCheckout() {
        return cartService.getProductsForCheckout();
    }

    @DeleteMapping("/items/{itemId}")
    ApiResponse<Void> deleteItemFromCart(@PathVariable("itemId") Long itemId) {
        cartService.deleteItemFromCart(itemId);
        return ApiResponse.<Void>builder().code(200).message("Item removed from cart").build();
    }

    @DeleteMapping("/{cartId}/items")
    ApiResponse<Void> clearAllItemsFromCart(@PathVariable("cartId") Long cartId) {
        cartService.clearAllItemsFromCart(cartId);
        return ApiResponse.<Void>builder().code(200).message("All items cleared from cart").build();
    }
}
