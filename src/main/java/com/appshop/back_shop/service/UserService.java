package com.appshop.back_shop.service;

import com.appshop.back_shop.domain.User;
import com.appshop.back_shop.dto.request.users.*;
import com.appshop.back_shop.dto.response.UserResponse;
import com.appshop.back_shop.exception.AppException;
import com.appshop.back_shop.exception.ErrorCode;
import com.appshop.back_shop.mapper.UserMapper;
import com.appshop.back_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    OTPService otpService;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public Long getUserIdFromToken() {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authenticationToken.getCredentials();
        return jwt.getClaim("userId");
    }

    public boolean changePassword(String email, String newPassword) {
        // Kiểm tra trạng thái OTP đã xác nhận
        if (!otpService.isOtpVerified(email)) {
            return false; // Không cho phép đổi mật khẩu nếu OTP chưa được xác nhận
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }

        // Mã hóa mật khẩu và lưu
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xoá trạng thái OTP sau khi thay đổi mật khẩu
        otpService.generateOTP(email); // Đảm bảo OTP cũ không dùng lại
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(Long userId) {
        return userMapper.toUserResponse(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    public UserResponse getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profileImg(user.getProfileImgUrl())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void updateLocationPreference(boolean isLocationEnable) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setIsLocationEnable(isLocationEnable);
        userRepository.save(user);
    }

    public UserResponse updateProfileUser(UpdateProfileRequest request) {
        Long userId = getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getProfileImg() != null) {
            user.setProfileImgUrl(request.getProfileImg());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profileImg(user.getProfileImgUrl())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }

    public UserResponse updateProfileNameUser(UpdateProfileNameRequest request) {
        Long userId = getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profileImg(user.getProfileImgUrl())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }

    public UserResponse updateProfileImgUser(UpdateProfileImgRequest request) {
        Long userId = getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getProfileImg() != null) {
            user.setProfileImgUrl(request.getProfileImg());
        }

        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profileImg(user.getProfileImgUrl())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }

    public UserResponse updateProfilePhoneUser(UpdateProfilePhoneRequest request) {
        Long userId = getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profileImg(user.getProfileImgUrl())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }

    public UserResponse updateProfileEmailUser(UpdateProfileEmailRequest request) {
        Long userId = getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .profileImg(user.getProfileImgUrl())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .build();
    }
}
