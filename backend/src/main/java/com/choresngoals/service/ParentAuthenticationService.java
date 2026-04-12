package com.choresngoals.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.AuthResponse;
import com.choresngoals.dto.LoginRequest;
import com.choresngoals.dto.UserResponse;
import com.choresngoals.entity.User;
import com.choresngoals.repository.UserRepository;
import com.choresngoals.security.JwtTokenService;

@Service
public class ParentAuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public ParentAuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse loginParent(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenService.getAccessTokenExpiresInSeconds(),
                toResponse(user)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
