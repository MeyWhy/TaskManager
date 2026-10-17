
package com.taskmanager.taskmanager_api.controller;

import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.dto.TaskRequest;
import com.taskmanager.taskmanager_api.dto.TaskResponse;
import com.taskmanager.taskmanager_api.service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public Page<TaskResponse> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean completed,
            Authentication authentication) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page index must not be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "id"));

        return service.getTasks(
                authentication.getName(), pageable, completed)
                .map(this::toResponse);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id,
            Authentication authentication) {

        return toResponse(
                service.getTaskById(id, authentication.getName()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {

        Task task = new Task(
                null,
                request.title(),
                request.description(),
                request.completed());

        return toResponse(
                service.createTask(task, authentication.getName()));
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {

        Task updatedTask = new Task(
                null,
                request.title(),
                request.description(),
                request.completed());

        return toResponse(
                service.updateTask(
                        id, updatedTask, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long id,
            Authentication authentication) {

        service.deleteTask(id, authentication.getName());
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted());
    }
}
