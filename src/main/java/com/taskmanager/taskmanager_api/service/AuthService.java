
package com.taskmanager.taskmanager_api.service;

import com.taskmanager.taskmanager_api.User;
import com.taskmanager.taskmanager_api.dto.AuthResponse;
import com.taskmanager.taskmanager_api.dto.RegisterRequest;
import com.taskmanager.taskmanager_api.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanager.taskmanager_api.dto.LoginRequest;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
}

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
String token = jwtService.generateToken(savedUser.getUsername());

return new AuthResponse(
    savedUser.getId(),
    savedUser.getUsername(),
    savedUser.getEmail(),
    token
);
    }



public AuthResponse login(LoginRequest request) {
    User user = userRepository.findByUsername(request.username())
            .orElseThrow(() ->
                    new org.springframework.security.authentication.BadCredentialsException(
                            "Invalid username or password"));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
        throw new org.springframework.security.authentication.BadCredentialsException(
                "Invalid username or password");
    }

    String token = jwtService.generateToken(user.getUsername());

    return new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            token
    );
}

}
