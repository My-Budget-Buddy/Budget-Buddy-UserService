package com.skillstorm.user_service.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String resource) {
        super(resource + " was not found");
    }
}
