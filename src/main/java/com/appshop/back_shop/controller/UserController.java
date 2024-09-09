package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.UserCreationRequest;
import com.appshop.back_shop.dto.request.UserUpdateRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.UserResponse;
import com.appshop.back_shop.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .code(200)
                .build();
    }

    @GetMapping("/myInfor")
    ApiResponse<UserResponse> getMyInfor(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfor())
                .code(200)
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") Long userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .code(200)
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .code(200)
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse deleteUser(@PathVariable Long userId){
        return ApiResponse.builder()
                .result(userService.deleteUser(userId))
                .code(200)
                .build();
    }
}
