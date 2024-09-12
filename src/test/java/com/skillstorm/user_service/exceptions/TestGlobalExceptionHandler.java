package com.skillstorm.user_service.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;

@WebMvcTest(TestGlobalExceptionHandler.class)
public class TestGlobalExceptionHandler {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }
    
    @Test
    public void testResourceNotFoundException() throws Exception {
        // Test the response from the exception handler
        ResponseEntity<?> response = globalExceptionHandler.resourceNotFoundException(new ResourceNotFoundException("User"));

        assertEquals("User was not found", response.getBody());
    }

    @Test
    public void testExistingAccountException() throws Exception {
        ResponseEntity<?> response = globalExceptionHandler.existingAccountException(new ExistingAccountException());

        assertEquals("An account with these credentials already exists", response.getBody());
    }

    @Test
    public void testIdMismatchException() throws Exception {
        ResponseEntity<?> response = globalExceptionHandler.idMismtachException(new IdMismatchException());

        assertEquals("Your account ID does not match the ID of the requested account", response.getBody());
    }
}
