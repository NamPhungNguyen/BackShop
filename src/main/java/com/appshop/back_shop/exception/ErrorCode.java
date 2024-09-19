package com.appshop.back_shop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "User existed",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1002, "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1006, "Invalid Refresh Token", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1007, "Refresh Token Expired", HttpStatus.UNAUTHORIZED),
    INVALID_KEY(1008, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1009, "You do not have permission", HttpStatus.FORBIDDEN),
    ACCOUNT_NOT_EXITS(1010, "Incorrect username or password", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1011, "User not found", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1012, "Category existed", HttpStatus.BAD_REQUEST),
    NAME_CATEGORY_INVALID(1013, "Name category is required", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1014, "Category not existed", HttpStatus.NOT_FOUND),
    PRODUCT_EXISTED(1015, "Product existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(1016, "Product not existed", HttpStatus.NOT_FOUND),
    CART_ALREADY_EXISTS(1017, "Cart already existed", HttpStatus.BAD_REQUEST),
    CART_NOT_EXISTS(1018, "Cart not existed", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND(1019, "Cart item not found", HttpStatus.NOT_FOUND),
    USER_NOT_AUTHORIZED(1020, "user not authorized", HttpStatus.UNAUTHORIZED),
    CART_EMPTY(1021, "cart is empty", HttpStatus.NO_CONTENT),
    STOCK_NOT_VALID(1022, "stock not valid", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1023, "Order not found", HttpStatus.NOT_FOUND),
    INVALID_PAYMENT_METHOD(1024, "Invalid payment method", HttpStatus.PAYMENT_REQUIRED),
    PAYMENT_NOT_FOUND(1025, "Payment not found", HttpStatus.NOT_FOUND),



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
