package com.appshop.back_shop.controller;

import com.appshop.back_shop.dto.request.AuthenticationRequest;
import com.appshop.back_shop.dto.request.IntrospectRequest;
import com.appshop.back_shop.dto.request.RefreshTokenRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.dto.response.AuthenticationResponse;
import com.appshop.back_shop.dto.response.IntrospectResponse;
import com.appshop.back_shop.dto.response.RefreshTokenResponse;
import com.appshop.back_shop.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .code(200)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<RefreshTokenResponse>builder()
                .result(result)
                .code(200)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .code(200)
                .build();
    }
}
