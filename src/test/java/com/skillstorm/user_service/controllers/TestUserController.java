package com.skillstorm.user_service.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.mappers.UserMapper;
import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.services.UserService;



@ExtendWith(MockitoExtension.class)
public class TestUserController {
    
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    UserMapper mapper = new UserMapper();

    private UserDto mockUser = new UserDto(1, "johndoe@gmail.com", "john", "doe");
    private UserDto mockUser2 = new UserDto(1, "janedoe@gmail.com", "jane", "doe");
    private User mockUser3 = new User(1, "johndoe@gmail.com", "john", "doe");


    @Test
    public void testFindAllAccounts() {
        
        Mockito.when(userService.findAllUsers()).thenReturn(List.of(mockUser, mockUser2));

        ResponseEntity<List<UserDto>> response = userController.findAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
    }
}
