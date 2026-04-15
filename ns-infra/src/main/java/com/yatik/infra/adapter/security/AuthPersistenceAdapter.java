package com.yatik.infra.adapter.security;

import com.yatik.domain.entity.User;
import com.yatik.domain.model.AuthTokenPair;
import com.yatik.domain.service.AuthService;
import com.yatik.domain.service.OtpType;
import com.yatik.infra.repository.SpringDataUserRepository;
import com.yatik.infra.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthPersistenceAdapter is an implementation of the AuthService interface,
 * responsible for handling user authentication and registration operations.
 * It interacts with the persistence layer and security-related components
 * to provide secure authentication functionality using JWT and password encoding mechanisms.
 *
 * This class utilizes:
 * - {@link SpringDataUserRepository} for accessing and persisting user data.
 * - {@link PasswordEncoder} for securely hashing user passwords.
 * - {@link JwtProvider} for generating and validating JWT tokens.
 * - {@link AuthenticationManager} for authenticating user credentials.
 *
 * Methods provided:
 * - register: Handles new user registration, hashes passwords, and issues JWT tokens.
 * - login: Authenticates existing users and generates a new token pair upon successful login.
 * - refreshToken: Refreshes the access token using a valid refresh token.
 * - sendOtp: Placeholder for OTP sending functionality.
 * - verifyOtp: Placeholder for OTP verification functionality.
 * - resetPassword: Placeholder for password reset functionality.
 */
@Service
@RequiredArgsConstructor
public class AuthPersistenceAdapter implements AuthService {

    private final SpringDataUserRepository jpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthTokenPair register(User user) {
        if (jpaRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("User already exists");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        jpaRepository.save(user);

        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        return new AuthTokenPair(accessToken, refreshToken);
    }

    @Override
    public AuthTokenPair login(String email, String password) {
        // Authenticate (Checks DB password vs. Input password)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String accessToken = jwtProvider.generateAccessToken(email);
        String refreshToken = jwtProvider.generateRefreshToken(email);
        return new AuthTokenPair(accessToken, refreshToken);
    }

    @Override
    public AuthTokenPair refreshToken(String refreshToken) {
        if (!jwtProvider.isTokenValid(refreshToken)) {
            throw new IllegalStateException("Invalid refresh token");
        }
        String subject = jwtProvider.extractUsername(refreshToken);
        String newRefreshToken = jwtProvider.generateRefreshToken(subject);
        String newAccessToken = jwtProvider.generateAccessToken(subject);
        return new AuthTokenPair(newAccessToken, newRefreshToken);
    }

    @Override
    public void sendOtp(String email, OtpType type) {
        throw new UnsupportedOperationException("Otp sending is not supported yet.");
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        throw new UnsupportedOperationException("Otp verification is not supported yet.");
    }

    @Override
    public void resetPassword(String email, String newPassword, String otp) {
        throw new UnsupportedOperationException("Password resetting is not supported yet.");
    }
}
