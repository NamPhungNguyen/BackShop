package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Cart;
import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.Cart.CartResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.repository.CartRepository;
import com.appshop.back_shop.repository.UserRepository;
import com.appshop.back_shop.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController()
@RequestMapping("/cart")
@Tag(name = "Cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
//    CartService cartService;
//
//    @PostMapping("/create")
//    public ApiResponse<CartResponse> createCart(){
//        return ApiResponse.<CartResponse>builder()
//                .result(cartService.createCart())
//                .build()
//    }

}
