package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.users.*;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.UserResponse;
import com.appshop.back_shop.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;

    @GetMapping("/list-user")
    ApiResponse<List<User>> getUsers() {
        return ApiResponse.<List<User>>builder().result(userService.getUsers()).message("User list retrieved successfully").code(200).build();
    }

    @GetMapping("/my-infor")
    ApiResponse<UserResponse> getMyInfor() {
        return ApiResponse.<UserResponse>builder().result(userService.getMyInfor()).code(200).build();
    }

    @GetMapping("/get-user/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        return ApiResponse.<UserResponse>builder().result(userService.getUserById(userId)).code(200).build();
    }

    @PutMapping("/update-user/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder().result(userService.updateUser(userId, request)).message("User updated successful").code(200).build();
    }

    @DeleteMapping("/delete-user/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<Void>builder().code(200).message("User deleted successful").build();
    }

    @PutMapping("/location-preference")
    public ApiResponse<Void> updateLocationPreference(@RequestBody LocationPreferenceRequest request) {
        userService.updateLocationPreference(request.getIsLocationEnable());
        return ApiResponse.<Void>builder().code(200).message("Success").build();
    }

    @PutMapping("/update-profile-user")
    public ApiResponse<UserResponse> updateProfileUser(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.updateProfileUser(request))
                .build();
    }

    @PutMapping("/update-profile-name-user")
    public ApiResponse<UserResponse> updateProfileNameUser(@RequestBody UpdateProfileNameRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.updateProfileNameUser(request))
                .build();
    }

    @PutMapping("/update-profile-img-user")
    public ApiResponse<UserResponse> updateProfileImgUser(@RequestBody UpdateProfileImgRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.updateProfileImgUser(request))
                .build();
    }

    @PutMapping("/update-profile-phone-user")
    public ApiResponse<UserResponse> updateProfilePhoneUser(@RequestBody UpdateProfilePhoneRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.updateProfilePhoneUser(request))
                .build();
    }

    @PutMapping("/update-profile-email-user")
    public ApiResponse<UserResponse> updateProfileEmailUser(@RequestBody UpdateProfileEmailRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .result(userService.updateProfileEmailUser(request))
                .build();
    }
}
