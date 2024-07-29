package com.git.exception.global;

import com.git.exception.RepositoryNotFoundException;
import com.git.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> userNotFoundExceptionHandler(UserNotFoundException ex, WebRequest request) {
        String path = request.getDescription(false).substring(4);
        return buildResponseEntity(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), path);
    }

    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> repositoryNotFoundExceptionHandler(RepositoryNotFoundException ex, WebRequest request) {
        String path = request.getDescription(false).substring(4);
        return buildResponseEntity(HttpStatus.NOT_FOUND, "Repository Not Found", ex.getMessage(), path);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> allExceptionsHandler(Exception ex, WebRequest request) {
        String path = request.getDescription(false).substring(4);
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), path);
    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(HttpStatus status, String error, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);
        return new ResponseEntity<>(body, status);
    }
}