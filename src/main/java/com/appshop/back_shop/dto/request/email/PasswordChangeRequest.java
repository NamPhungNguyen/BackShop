package com.appshop.back_shop.dto.request.email;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String email;
    private String newPassword;
}
