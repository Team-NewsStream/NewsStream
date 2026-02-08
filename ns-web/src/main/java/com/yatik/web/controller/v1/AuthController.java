package com.yatik.web.controller.v1;

import com.yatik.domain.entity.User;
import com.yatik.domain.model.AuthTokenPair;
import com.yatik.domain.service.AuthService;
import com.yatik.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthController {

    private final AuthService authService;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-zA-Z]).{6,}$");

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.")
    public ResponseEntity<?> signup(@RequestBody UserCreateRequest request) {
        if (request.password().isBlank() || request.email().isBlank() || request.name().isBlank()) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        if (!PASSWORD_PATTERN.matcher(request.password()).matches()) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters long and contain at least one letter, one number, and one special character");
        }
        
        User user = User.builder()
                .email(request.email())
                .name(request.name())
                .passwordHash(request.password()) // Service layer handles hashing
                .build();

        try {
            AuthTokenPair tokens = authService.register(user);
            return ResponseEntity.ok(toTokenResponse(tokens));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns access and refresh tokens.")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthTokenPair tokens = authService.login(request.username(), request.password());
            return ResponseEntity.ok(toTokenResponse(tokens));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Generates a new access token using a valid refresh token.")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthTokenPair tokens = authService.refreshToken(request.refreshToken());
            return ResponseEntity.ok(toTokenResponse(tokens));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).build();
        }
    }

    private static TokenResponse toTokenResponse(AuthTokenPair tokens) {
        return new TokenResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                "Bearer"
        );
    }
}