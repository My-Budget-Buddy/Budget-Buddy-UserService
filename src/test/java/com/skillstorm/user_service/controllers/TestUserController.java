package com.skillstorm.user_service.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import com.skillstorm.user_service.exceptions.ResourceNotFoundException;
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

    private UserDto mockUserDto = new UserDto(1, "johndoe@gmail.com", "john", "doe");
    private UserDto mockUserDto2 = new UserDto(2, "janedoe@gmail.com", "jane", "doe");
    private User mockUser = new User(3, "johndoe@gmail.com", "john", "doe");


    @Test
    public void testFindAllUsers() {
        
        Mockito.when(userService.findAllUsers()).thenReturn(List.of(mockUserDto, mockUserDto2));

        ResponseEntity<List<UserDto>> response = userController.findAllUsers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    public void testFindUserById() {
        
        Mockito.when(userService.findById(Mockito.anyInt()))
                .thenReturn(mockUserDto);

        // String.valueOf(Mockito.anyInt()) is being used because the controller method specifically needs a string that can be converted into a number. Just using Mockito.anyString() would result in strings that are not valid ints
        ResponseEntity<UserDto> response = userController.findUserById(String.valueOf(Mockito.anyInt()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockUserDto);
    }

    @Test
    public void testFindUserByIdUnsuccessful() {

        Mockito.when(userService.findById(Mockito.anyInt()))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> userController.findUserById(String.valueOf(Mockito.anyInt())));
    }

    @Test
    public void testCreateUser() {

        Mockito.when(userService.createUser(mockUser))
                .thenReturn(mockUserDto);
                
        ResponseEntity<UserDto> response = userController.createUser(mockUser);
    
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(mockUserDto);
    }

    @Test
    public void testUpdateUser() {

        Mockito.doNothing()
                .when(userService).compareHeaderIdWithRequestedDataId(Mockito.anyInt(), Mockito.anyString());

        Mockito.when(userService.updateUser(mockUser))
                .thenReturn(mockUserDto);

        ResponseEntity<UserDto> response = userController.updateUser(mockUser, "1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(mockUserDto);
    }

    @Test
    public void testUpdateUserUnsuccessful() {

        Mockito.doNothing()
                .when(userService).compareHeaderIdWithRequestedDataId(Mockito.anyInt(), Mockito.anyString());

        Mockito.when(userService.updateUser(mockUser)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> userController.updateUser(mockUser, "1"));
    }

    @Test
    public void testDeleteUser() {

        Mockito.doNothing().when(userService).deleteUser(Mockito.anyInt());

        ResponseEntity<Void> response = userController.deleteUser(String.valueOf(Mockito.anyInt()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testDeleteUserUnsuccessful() {

        Mockito.doThrow(ResourceNotFoundException.class).when(userService).deleteUser(Mockito.anyInt());

        assertThrows(ResourceNotFoundException.class, () -> userController.deleteUser(String.valueOf(Mockito.anyInt())));
    }
}
