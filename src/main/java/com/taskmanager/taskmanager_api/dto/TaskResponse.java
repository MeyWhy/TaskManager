
package com.taskmanager.taskmanager_api.dto;

public record TaskResponse(
        Long id,
        String title,
        String description,
        boolean completed
) {}
