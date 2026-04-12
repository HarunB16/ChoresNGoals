package com.choresngoals.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.CreateTaskRequest;
import com.choresngoals.dto.TaskResponse;
import com.choresngoals.entity.Child;
import com.choresngoals.entity.Task;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.ChildRepository;
import com.choresngoals.repository.TaskRepository;
import com.choresngoals.security.AuthenticatedUser;

@Service
public class TaskManagementService {

    private final ChildRepository childRepository;
    private final TaskRepository taskRepository;

    public TaskManagementService(ChildRepository childRepository, TaskRepository taskRepository) {
        this.childRepository = childRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public TaskResponse createTask(AuthenticatedUser authenticatedUser, UUID childId, CreateTaskRequest request) {
        ensureParent(authenticatedUser);

        Child child = childRepository.findByIdAndParentId(childId, authenticatedUser.id())
                .orElseThrow(() -> new ChildNotFoundException("Child was not found"));

        Task task = new Task(
                child,
                request.title().trim(),
                normalizeDescription(request.description()),
                request.type(),
                request.points(),
                request.dueDate()
        );

        return toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> listTasks(AuthenticatedUser authenticatedUser, UUID childId) {
        ensureParent(authenticatedUser);
        ensureChildBelongsToParent(childId, authenticatedUser.id());

        return taskRepository.findAllByChildIdAndChildParentIdOrderByCreatedAtDesc(childId, authenticatedUser.id())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void ensureParent(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.role() != UserRole.PARENT) {
            throw new ForbiddenOperationException("Only parent accounts can manage tasks");
        }
    }

    private void ensureChildBelongsToParent(UUID childId, UUID parentId) {
        if (childRepository.findByIdAndParentId(childId, parentId).isEmpty()) {
            throw new ChildNotFoundException("Child was not found");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return "";
        }

        return description.trim();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getChild().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getType(),
                task.getStatus(),
                task.getPoints(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }
}
