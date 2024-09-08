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

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){
        return ApiResponse.<User>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    List<User> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return userService.getUsers();
    }
    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId){
        return userService.getUserById(userId);
    }
    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
    }
}
