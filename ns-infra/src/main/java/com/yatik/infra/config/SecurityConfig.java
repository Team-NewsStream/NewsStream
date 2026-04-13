package com.yatik.infra.config;

import com.yatik.infra.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for defining security policies, filters, and authentication mechanisms.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain for the application.
     * Defines the authentication and authorization settings, disables CSRF protection,
     * and configures the session management policy.
     *
     * @param http The {@link HttpSecurity} instance used to configure security settings.
     * @return A {@link SecurityFilterChain} instance representing the configured security filter chain.
     * @throws Exception If there is an error during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/v1/signup", "/v1/login", "/v1/refresh").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/v1/refresh-news").permitAll()
                    .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides a bean definition for a PasswordEncoder, intended for use in
     * encoding and verifying passwords in the application.
     *
     * @return An instance of BCryptPasswordEncoder, a password encoder that
     *         uses the BCrypt hashing function for secure password handling.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures and provides an {@link AuthenticationManager} bean for handling authentication logic
     * within the security configuration of the application.
     *
     * @param config The {@link AuthenticationConfiguration} instance providing the necessary configuration
     *               to initialize the {@link AuthenticationManager}.
     * @return An instance of {@link AuthenticationManager} for managing authentication processes.
     * @throws Exception If an error occurs during the retrieval of the {@link AuthenticationManager}.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
