
package com.taskmanager.taskmanager_api.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.taskmanager.taskmanager_api.exception.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .distinct()
                .toList();

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
                details
        );
    }

@ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
public ResponseEntity<ApiError> handleBadCredentials(
        org.springframework.security.authentication.BadCredentialsException exception) {
    return buildError(
            HttpStatus.UNAUTHORIZED,
            "Invalid username or password",
            List.of()
    );
}

@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ApiError> handleIllegalArgument(
        IllegalArgumentException exception) {

    return buildError(
            HttpStatus.CONFLICT,
            exception.getMessage(),
            List.of()
    );
}

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            TaskNotFoundException ex) {

        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidBody(
            HttpMessageNotReadableException ex) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or contains invalid JSON",
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(
            Exception ex) {

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                List.of()
        );
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            List<String> details) {

        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );

        return ResponseEntity.status(status).body(body);
    }
}
