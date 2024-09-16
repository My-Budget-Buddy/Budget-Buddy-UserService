package com.skillstorm.user_service.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.exceptions.IdMismatchException;
import com.skillstorm.user_service.exceptions.ResourceNotFoundException;
import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.repositories.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class TestUserServiceIntegration {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindAllUsers_Success() {
        User user1 = new User(1, "email1@example.com", "John", "Doe");
        User user2 = new User(2, "email2@example.com", "Jane", "Doe");
        userRepository.saveAll(List.of(user1, user2));

        List<UserDto> result = userService.findAllUsers();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("email1@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("email2@example.com")));
    }
    
    @Test
    public void testFindAllUsers_NoUsersFound() {
        List<UserDto> result = userService.findAllUsers();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindById_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");
        userRepository.save(user);

        UserDto result = userService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("email@example.com", result.getEmail());
    }

    @Test
    public void testFindById_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(999));
    }

    @Test
    public void testCreateUser_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");

        UserDto result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("email@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");
        userRepository.save(user);

        User updatedUser = new User(1, "email@example.com", "Jane", "Doe");
        UserDto result = userService.updateUser(updatedUser);

        assertEquals("Jane", result.getFirstName());
    }

    @Test
    public void testUpdateUser_NotFound() {
        User user = new User(1, "email@example.com", "John", "Doe");

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    public void testDeleteUser_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");
        userRepository.save(user);

        userService.deleteUser(1);

        Optional<User> result = userRepository.findById(1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteUser_NotFound() {
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999));
    }

    @Test
    public void testCompareHeaderIdWithRequestedDataId_Success() {
        assertDoesNotThrow(() -> userService.compareHeaderIdWithRequestedDataId(1, "1"));
    }

    @Test
    public void testCompareHeaderIdWithRequestedDataId_IdMismatch() {
        assertThrows(IdMismatchException.class, () -> userService.compareHeaderIdWithRequestedDataId(1, "2"));
    }
}
