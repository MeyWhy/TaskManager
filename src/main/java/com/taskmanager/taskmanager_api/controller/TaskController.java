
package com.taskmanager.taskmanager_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.taskmanager.taskmanager_api.Task;
import com.taskmanager.taskmanager_api.dto.TaskRequest;
import com.taskmanager.taskmanager_api.dto.TaskResponse;
import com.taskmanager.taskmanager_api.service.TaskService;

import jakarta.validation.Valid;

@Tag(
    name = "Tasks",
    description = "Operations for creating, retrieving, updating, and deleting tasks"
)

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @Operation(
    summary = "Get tasks",
    description = "Returns a paginated list of tasks. Optionally filter by completion status."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Tasks retrieved successfully"
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid pagination parameters",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    )
})
    @GetMapping
    public Page<TaskResponse> getAllTasks(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0") int page,
                    
            @Parameter(description = "Number of tasks per page (1–100)", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Filter by completion status; omit for all tasks", example = "false")
            @RequestParam(required = false) Boolean completed) {

        // Prevent invalid pagination values
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        return service.getTasks(pageable, completed)
                .map(this::toResponse);
    }


    @Operation(
    summary = "Get a task by ID",
    description = "Retrieves a single task using its unique identifier."
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Task found"),
    @ApiResponse(
        responseCode = "404",
        description = "Task not found",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    )
})
    @GetMapping("/{id}")
    public TaskResponse getTaskById(
        @Parameter(description = "Unique task identifier", example = "1")
        @PathVariable Long id) {
        return toResponse(service.getTaskById(id));
    }


    @Operation(
    summary = "Create a task",
    description = "Creates a new task after validating the request body."
)
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Task created successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request body or validation failure",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    )
})

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
         @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Task details to create",
            required = true
        )
        @Valid @RequestBody TaskRequest request) {
        Task task = toEntity(request);
        Task savedTask = service.createTask(task);
        return toResponse(savedTask);
    }

    @Operation(
    summary = "Update a task",
    description = "Updates the title, description, and completion status of an existing task."
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Task updated successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request body or validation failure",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Task not found",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    )
})


    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        Task task = toEntity(request);
        Task updatedTask = service.updateTask(id, task);
        return toResponse(updatedTask);
    }


    @Operation(
    summary = "Delete a task",
    description = "Permanently deletes an existing task."
)
@ApiResponses({
    @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
    @ApiResponse(
        responseCode = "404",
        description = "Task not found",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(
            schema = @Schema(
                implementation = com.taskmanager.taskmanager_api.exception.ApiError.class
            )
        )
    )
})

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
        @Parameter(description = "Unique task identifier", example = "1")
        @PathVariable Long id) {
        service.deleteTask(id);
    }

    private Task toEntity(TaskRequest request) {
        return new Task(
                null,
                request.title(),
                request.description(),
                request.completed()
        );
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted()
        );
    }
}
