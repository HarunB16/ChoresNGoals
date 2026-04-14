package com.choresngoals.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choresngoals.entity.Child;

public interface ChildRepository extends JpaRepository<Child, UUID> {

    List<Child> findAllByParentIdOrderByCreatedAtDesc(UUID parentId);

    List<Child> findAllByFamilyIdOrderByCreatedAtDesc(UUID familyId);

    Optional<Child> findByIdAndParentId(UUID id, UUID parentId);

    Optional<Child> findByIdAndFamilyId(UUID id, UUID familyId);
}
