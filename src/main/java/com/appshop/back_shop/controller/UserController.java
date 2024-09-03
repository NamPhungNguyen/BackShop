package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.UserCreationRequest;
import com.appshop.back_shop.dto.request.UserUpdateRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<User> apiResponse = new ApiResponse<>();

        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping
    List<User> getUsers(){
        return userService.getUsers();
    }
    @GetMapping("/{userId}")
    User getUserById(@PathVariable("userId") Long userId){
        return userService.getUserById(userId);
    }
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
    }
}
