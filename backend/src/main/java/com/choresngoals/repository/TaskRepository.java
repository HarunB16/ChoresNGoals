package com.choresngoals.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choresngoals.entity.Task;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByChildIdAndChildParentIdOrderByCreatedAtDesc(UUID childId, UUID parentId);
}
