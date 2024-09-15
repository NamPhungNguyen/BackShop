package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.response.ApiResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController()
@RequestMapping("/cart")
@Tag(name = "Cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping("/create")
    ApiResponse<CartResponse> createCart(){
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authenticationToken.getCredentials();

        Long userId = jwt.getClaim("userId");


        return ApiResponse.<CartResponse>builder()
                .result(cartService.createCart(userId))
                .code(200)
                .message("Create cart for user successfully")
                .build();
    }

}
