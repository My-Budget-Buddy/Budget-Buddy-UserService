package com.skillstorm.user_service.exceptions;

public class ExistingAccountException extends RuntimeException {
    
    public ExistingAccountException() {
        super("An account with these credentials already exists");
    }
}
