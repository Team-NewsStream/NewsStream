package com.yatik.domain.service;

import com.yatik.domain.entity.User;
import com.yatik.domain.model.AuthTokenPair;

public interface AuthService {
    AuthTokenPair register(User user);
    AuthTokenPair login(String email, String password);
    AuthTokenPair refreshToken(String refreshToken);

    // Future-proofing for OTP
    void sendOtp(String email, OtpType type);
    boolean verifyOtp(String email, String otp);
    void resetPassword(String email, String newPassword, String otp);
}