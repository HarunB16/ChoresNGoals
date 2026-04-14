package com.choresngoals.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.choresngoals.dto.CreateTaskRequest;
import com.choresngoals.dto.TaskResponse;
import com.choresngoals.entity.Child;
import com.choresngoals.entity.Family;
import com.choresngoals.entity.Task;
import com.choresngoals.entity.User;
import com.choresngoals.entity.UserRole;
import com.choresngoals.repository.ChildRepository;
import com.choresngoals.repository.TaskRepository;
import com.choresngoals.repository.UserRepository;
import com.choresngoals.security.AuthenticatedUser;

@Service
public class TaskManagementService {

    private final ChildRepository childRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FamilyStructureService familyStructureService;

    public TaskManagementService(
            ChildRepository childRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            FamilyStructureService familyStructureService
    ) {
        this.childRepository = childRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.familyStructureService = familyStructureService;
    }

    @Transactional
    public TaskResponse createTask(AuthenticatedUser authenticatedUser, UUID childId, CreateTaskRequest request) {
        ensureParent(authenticatedUser);
        Family family = ensureParentFamily(authenticatedUser);

        Child child = childRepository.findByIdAndFamilyId(childId, family.getId())
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

    @Transactional
    public List<TaskResponse> listTasks(AuthenticatedUser authenticatedUser, UUID childId) {
        ensureParent(authenticatedUser);
        Family family = ensureParentFamily(authenticatedUser);
        ensureChildBelongsToFamily(childId, family.getId());

        return taskRepository.findAllByChildIdAndChildFamilyIdOrderByCreatedAtDesc(childId, family.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void ensureParent(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.role() != UserRole.PARENT) {
            throw new ForbiddenOperationException("Only parent accounts can manage tasks");
        }
    }

    private Family ensureParentFamily(AuthenticatedUser authenticatedUser) {
        User parent = userRepository.findById(authenticatedUser.id())
                .orElseThrow(() -> new ForbiddenOperationException("Authenticated parent was not found"));

        return familyStructureService.ensureFamilyForParentWithChildren(parent);
    }

    private void ensureChildBelongsToFamily(UUID childId, UUID familyId) {
        if (childRepository.findByIdAndFamilyId(childId, familyId).isEmpty()) {
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
