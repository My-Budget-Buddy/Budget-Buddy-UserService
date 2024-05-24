package com.skillstorm.user_service.exceptions;

public class IdMismatchException extends RuntimeException {
    
    public IdMismatchException() {
        super("Your account ID does not match the ID of the requested account");
    }
}
