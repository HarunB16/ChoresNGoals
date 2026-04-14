package com.choresngoals.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choresngoals.entity.FamilyMembership;
import com.choresngoals.entity.FamilyRole;

public interface FamilyMembershipRepository extends JpaRepository<FamilyMembership, UUID> {

    Optional<FamilyMembership> findFirstByUserIdAndRole(UUID userId, FamilyRole role);

    boolean existsByFamilyIdAndUserIdAndRole(UUID familyId, UUID userId, FamilyRole role);

    boolean existsByFamilyIdAndChildIdAndRole(UUID familyId, UUID childId, FamilyRole role);
}
