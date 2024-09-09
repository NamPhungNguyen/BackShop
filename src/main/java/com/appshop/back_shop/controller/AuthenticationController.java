package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.AuthenticationRequest;
import com.appshop.back_shop.dto.request.IntrospectRequest;
import com.appshop.back_shop.dto.request.RefreshTokenRequest;
import com.appshop.back_shop.dto.request.UserCreationRequest;
import com.appshop.back_shop.dto.response.*;
import com.appshop.back_shop.service.AuthenticationService;
import com.appshop.back_shop.service.UserService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;
    UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nhập vào tên người dùng và mật khẩu để đăng nhập")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .code(200)
                .build();
    }

    @PostMapping("/signup")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .code(200)
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<RefreshTokenResponse>builder()
                .result(result)
                .code(200)
                .build();
    }

    @PostMapping("/introspect")
    @Operation(summary = "Xác thực token")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .code(200)
                .build();
    }
}
