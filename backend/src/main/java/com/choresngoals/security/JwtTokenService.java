package com.choresngoals.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.choresngoals.entity.User;
import com.choresngoals.entity.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = createSigningKey(jwtProperties.secret());
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(getAccessTokenExpiresInSeconds());

        return Jwts.builder()
                .issuer(jwtProperties.issuer())
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public AuthenticatedUser parseAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(jwtProperties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new AuthenticatedUser(
                UUID.fromString(claims.getSubject()),
                claims.get("email", String.class),
                UserRole.valueOf(claims.get("role", String.class))
        );
    }

    public long getAccessTokenExpiresInSeconds() {
        return jwtProperties.accessTokenMinutes() * 60;
    }

    private SecretKey createSigningKey(String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be set and contain at least 32 characters");
        }

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
