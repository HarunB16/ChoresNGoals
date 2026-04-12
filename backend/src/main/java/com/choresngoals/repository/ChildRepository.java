package com.choresngoals.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choresngoals.entity.Child;

public interface ChildRepository extends JpaRepository<Child, UUID> {

    List<Child> findAllByParentIdOrderByCreatedAtDesc(UUID parentId);

    Optional<Child> findByIdAndParentId(UUID id, UUID parentId);
}
