package com.skillstorm.user_service.controllers;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.repositories.UserRepository;
import com.skillstorm.user_service.services.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestUserControllerIntegration {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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

        ResponseEntity<UserDto[]> response = restTemplate.getForEntity("/users", UserDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    public void testFindUserById_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-ID", "1");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<UserDto> response = restTemplate.exchange("/users/user", HttpMethod.GET, requestEntity, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    public void testFindUserById_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-ID", "999");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/users/user", HttpMethod.GET, requestEntity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateUser_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");

        HttpEntity<User> requestEntity = new HttpEntity<>(user);
        ResponseEntity<UserDto> response = restTemplate.postForEntity("/users", requestEntity, UserDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("email@example.com", response.getBody().getEmail());
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-ID", "1");

        User updatedUser = new User(1, "email@example.com", "Jane", "Doe");
        HttpEntity<User> requestEntity = new HttpEntity<>(updatedUser, headers);

        ResponseEntity<UserDto> response = restTemplate.exchange("/users", HttpMethod.PUT, requestEntity, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Jane", response.getBody().getFirstName());
    }

    @Test
    public void testUpdateUser_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-ID", "1");

        User user = new User(1, "email@example.com", "John", "Doe");
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);

        ResponseEntity<String> response = restTemplate.exchange("/users", HttpMethod.PUT, requestEntity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteUser_Success() {
        User user = new User(1, "email@example.com", "John", "Doe");
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-ID", "1");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange("/users", HttpMethod.DELETE, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<User> result = userRepository.findById(1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteUser_NotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-ID", "999");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/users", HttpMethod.DELETE, requestEntity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
