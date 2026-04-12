package com.choresngoals.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.entity.RefreshToken;
import com.choresngoals.entity.User;
import com.choresngoals.repository.RefreshTokenRepository;
import com.choresngoals.security.JwtProperties;

@Service
public class RefreshTokenService {

    private static final int REFRESH_TOKEN_BYTES = 64;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public String createRefreshToken(User user) {
        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);
        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.refreshTokenDays() * 24 * 60 * 60);

        refreshTokenRepository.save(new RefreshToken(user, tokenHash, expiresAt));

        return rawToken;
    }

    private String generateRawToken() {
        byte[] tokenBytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
