package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.email.EmailRequest;
import com.appshop.back_shop.dto.request.email.ForgotPasswordRequest;
import com.appshop.back_shop.dto.request.email.OTPValidationRequest;
import com.appshop.back_shop.dto.request.email.PasswordChangeRequest;
import com.appshop.back_shop.dto.request.users.*;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.UserResponse;
import com.appshop.back_shop.service.EmailService;
import com.appshop.back_shop.service.OTPService;
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
    EmailService emailService;
    OTPService otpService;

    @GetMapping("/list-user")
    ApiResponse<List<User>> getUsers() {
        return ApiResponse.<List<User>>builder().result(userService.getUsers()).message("User list retrieved successfully").code(200).build();
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        boolean result = userService.changePassword(request.getEmail(), request.getNewPassword());
        if (result) {
            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("Password changed successfully")
                    .build();
        }
        return ApiResponse.<Void>builder()
                .code(400)
                .message("OTP not verified or password change failed")
                .build();
    }

    @PostMapping("/send-email")
    ApiResponse<Void> sendEmail(@RequestBody EmailRequest emailRequest) {
        emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
        return ApiResponse.<Void>builder().code(200).message("Email sent successfully").build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();

        if (otpService.isEmailRegistered(email)) {
            String otp = otpService.generateOTP(email);
            emailService.sendEmail(email, "Mã OTP xác nhận", "Mã OTP của bạn là: " + otp);
            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("OTP đã được gửi đến email của bạn")
                    .build();
        }

        return ApiResponse.<Void>builder()
                .code(400)
                .message("Email không tồn tại trong hệ thống")
                .build();
    }

    @PostMapping("/validate-otp")
    public ApiResponse<Void> validateOTP(@RequestBody OTPValidationRequest request) {
        boolean valid = otpService.validateOTP(request.getEmail(), request.getOtp());

        if (valid) {
            return ApiResponse.<Void>builder()
                    .code(200)
                    .message("OTP hợp lệ")
                    .build();
        }

        return ApiResponse.<Void>builder()
                .code(400)
                .message("OTP không chính xác")
                .build();
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
        return ApiResponse.<UserResponse>builder().code(200).result(userService.updateProfileUser(request)).build();
    }

    @PutMapping("/update-profile-name-user")
    public ApiResponse<UserResponse> updateProfileNameUser(@RequestBody UpdateProfileNameRequest request) {
        return ApiResponse.<UserResponse>builder().code(200).result(userService.updateProfileNameUser(request)).build();
    }

    @PutMapping("/update-profile-img-user")
    public ApiResponse<UserResponse> updateProfileImgUser(@RequestBody UpdateProfileImgRequest request) {
        return ApiResponse.<UserResponse>builder().code(200).result(userService.updateProfileImgUser(request)).build();
    }

    @PutMapping("/update-profile-phone-user")
    public ApiResponse<UserResponse> updateProfilePhoneUser(@RequestBody UpdateProfilePhoneRequest request) {
        return ApiResponse.<UserResponse>builder().code(200).result(userService.updateProfilePhoneUser(request)).build();
    }

    @PutMapping("/update-profile-email-user")
    public ApiResponse<UserResponse> updateProfileEmailUser(@RequestBody UpdateProfileEmailRequest request) {
        return ApiResponse.<UserResponse>builder().code(200).result(userService.updateProfileEmailUser(request)).build();
    }
}
