package com.skillstorm.user_service.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {
    User user;
    String email;
    String firstName;
    String lastName;
    Integer id;

    @BeforeEach
    public void init() {
        System.out.println("Before Testing: Initializing ");
        user = new User();
    }

    @Test
    void testEmail() {
        // Arrange
        email = "tester@email.com";
        // Act
        User actual = user.email(email);
        // Assert
        assertEquals(email, actual.getEmail());
        assertEquals(user, actual);
        
    }

    @Test
    void testFirstName() {
        // Arrange
        firstName = "John";
        // Act
        User actual = user.firstName(firstName);
        // Assert
        assertEquals(firstName, actual.getFirstName());
        assertEquals(user, actual);
    }

    @Test
    void testId() {
       // Arrange
       id = 1;
       // Act
       User actual = user.id(id);
       // Assert
       assertEquals(id, actual.getId());
       assertEquals(user, actual); 
    }

    @Test
    void testLastName() {
        // Assert
        lastName = "Smith";
        // Act
        User actual = user.lastName(lastName);
        // Assert
        assertEquals(lastName, actual.getLastName());
        assertEquals(user, actual);
    }
}
