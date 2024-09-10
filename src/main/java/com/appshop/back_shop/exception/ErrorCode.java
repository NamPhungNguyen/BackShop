package com.appshop.back_shop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "user existed",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1002, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "user not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1006, "Invalid Refresh Token", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1007, "Refresh Token Expired", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1008, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1009, "You do dot have permission", HttpStatus.FORBIDDEN)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
