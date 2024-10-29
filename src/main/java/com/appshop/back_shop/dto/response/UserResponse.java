package com.appshop.back_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    String fullName;
    String email;
    String phoneNumber;
    String profileImg;
    Set<String> roles;
}
