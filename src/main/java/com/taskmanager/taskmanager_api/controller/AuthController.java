
package com.taskmanager.taskmanager_api.controller;

import com.taskmanager.taskmanager_api.dto.AuthResponse;
import com.taskmanager.taskmanager_api.dto.RegisterRequest;
import com.taskmanager.taskmanager_api.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.taskmanager.taskmanager_api.dto.LoginRequest;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Register a user",
        description = "Creates a new account with a securely hashed password."
    )
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }


@PostMapping("/login")
@Operation(summary = "Log in", description = "Authenticates a user and returns a JWT.")
public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
}

}
