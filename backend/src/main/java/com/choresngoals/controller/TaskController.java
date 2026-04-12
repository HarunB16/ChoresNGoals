package com.choresngoals.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choresngoals.dto.CreateTaskRequest;
import com.choresngoals.dto.TaskResponse;
import com.choresngoals.security.AuthenticatedUser;
import com.choresngoals.service.TaskManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/children/{childId}/tasks")
public class TaskController {

    private final TaskManagementService taskManagementService;

    public TaskController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID childId,
            @Valid @RequestBody CreateTaskRequest request
    ) {
        TaskResponse response = taskManagementService.createTask(authenticatedUser, childId, request);

        return ResponseEntity
                .created(URI.create("/api/children/" + childId + "/tasks/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable UUID childId
    ) {
        return ResponseEntity.ok(taskManagementService.listTasks(authenticatedUser, childId));
    }
}
