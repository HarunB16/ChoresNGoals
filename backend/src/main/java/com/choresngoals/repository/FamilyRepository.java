package com.choresngoals.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choresngoals.entity.Family;

public interface FamilyRepository extends JpaRepository<Family, UUID> {
}
