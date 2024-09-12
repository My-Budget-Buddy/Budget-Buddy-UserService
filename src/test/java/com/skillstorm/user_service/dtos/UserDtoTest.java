package com.skillstorm.user_service.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDtoTest {
    UserDto user;
    String email;
    String firstName;
    String lastName;
    Integer id;

    @BeforeEach
    public void init() {
        System.out.println("Before Testing: Initializing ");
        user = new UserDto();
    }

    @Test
    void testEmail() {
        // Arrange
        email = "tester@email.com";
        // Act
        UserDto actual = user.email(email);
        // Assert
        assertEquals(email, actual.getEmail());
        assertEquals(user, actual);
    }

    @Test
    void testFirstName() {
        // Arrange
        firstName = "John";
        // Act
        UserDto actual = user.firstName(firstName);
        // Assert
        assertEquals(firstName, actual.getFirstName());
        assertEquals(user, actual);
    }

    @Test
    void testId() {
        // Arrange
        id = 1;
        // Act
        UserDto actual = user.id(id);
        // Assert
        assertEquals(id, actual.getId());
        assertEquals(user, actual);
    }

    @Test
    void testLastName() {
        // Assert
        lastName = "Smith";
        // Act
        UserDto actual = user.lastName(lastName);
        // Assert
        assertEquals(lastName, actual.getLastName());
        assertEquals(user, actual);
    }
}
