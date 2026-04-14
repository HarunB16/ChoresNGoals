package com.choresngoals.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choresngoals.entity.ChildInvitation;

public interface ChildInvitationRepository extends JpaRepository<ChildInvitation, UUID> {

    boolean existsByToken(String token);

    Optional<ChildInvitation> findByToken(String token);
}
