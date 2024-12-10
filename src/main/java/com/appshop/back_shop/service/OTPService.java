package com.appshop.back_shop.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OTPService {

    private Map<String, String> otpStorage = new HashMap<>();
    private Set<String> verifiedEmails = new HashSet<>();

    // Kiểm tra email đã đăng ký hay không
    public boolean isEmailRegistered(String email) {
        return true; // Giả định email đã đăng ký
    }

    // Tạo OTP
    public String generateOTP(String email) {
        String otp = String.valueOf(new Random().nextInt(9000) + 1000);
        otpStorage.put(email, otp);

        // Tự động xoá OTP sau 5 phút
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                otpStorage.remove(email);
                verifiedEmails.remove(email); // Loại bỏ trạng thái xác nhận
            }
        }, 5 * 60 * 1000);

        return otp;
    }

    // Kiểm tra OTP hợp lệ
    public boolean validateOTP(String email, String otp) {
        boolean isValid = otpStorage.containsKey(email) && otpStorage.get(email).equals(otp);
        if (isValid) {
            verifiedEmails.add(email); // Đánh dấu email đã xác nhận
        }
        return isValid;
    }

    // Kiểm tra email đã xác nhận OTP
    public boolean isOtpVerified(String email) {
        return verifiedEmails.contains(email);
    }
}
