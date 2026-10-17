
package com.taskmanager.taskmanager_api.dto;

public record AuthResponse(
    Long id,
    String username,
    String email,
    String token
) {}
