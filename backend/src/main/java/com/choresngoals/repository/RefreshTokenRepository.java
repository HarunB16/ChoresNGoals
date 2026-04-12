package com.choresngoals.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choresngoals.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
}
