package com.skillstorm.user_service.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException exception) {

        return ResponseEntity.status(404).body(exception.getMessage());
    }
    
    @ExceptionHandler(ExistingAccountException.class)
    public ResponseEntity<?> existingAccountException(ExistingAccountException exception) {

        return ResponseEntity.status(400).body(exception.getMessage());
    }

    @ExceptionHandler(IdMismatchException.class)
    public ResponseEntity<?> idMismtachException(IdMismatchException exception) {

        return ResponseEntity.status(401).body(exception.getMessage());
    }
}
