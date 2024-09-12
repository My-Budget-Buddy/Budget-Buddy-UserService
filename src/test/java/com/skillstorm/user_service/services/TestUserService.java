package com.skillstorm.user_service.services;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.exceptions.IdMismatchException;
import com.skillstorm.user_service.exceptions.ResourceNotFoundException;
import com.skillstorm.user_service.mappers.UserMapper;
import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.repositories.UserRepository;

public class TestUserService {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAllUsers() {
        // try to find multiple users
        User user1 = new User(1, "test1@example.com", "John", "Doe");
        User user2 = new User(2, "test2@example.com", "Jane", "Doe");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        UserDto userDto1 = new UserDto();
        userDto1.setId(1);
        userDto1.setEmail("test1@example.com");
        userDto1.setFirstName("John");
        userDto1.setLastName("Doe");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2);
        userDto2.setEmail("test2@example.com");
        userDto2.setFirstName("Jane");
        userDto2.setLastName("Doe");

        when(userMapper.toDto(user1)).thenReturn(userDto1);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.findAllUsers();

        assertEquals(2, result.size());
        assertEquals(userDto1, result.get(0));
        assertEquals(userDto2, result.get(1));
    }

    @Test
    public void testFindUserById() {
        User user = new User(1, "test@example.com", "John", "Doe");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setId(user.getId());
        
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.findById(1);

        assertEquals(userDto, result);
    }

    @Test
    public void testFindUserByIdButNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findById(1);
        });

        assertEquals(ResourceNotFoundException.class, exception.getClass());
    }

    @Test
    public void testCreateUser() {
        User user = new User(1, "test@example.com", null, null);
        when(userRepository.save(user)).thenReturn(user);

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("test@example.com");

        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(user);

        assertEquals(userDto, result);
        assertEquals(1, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }


    @Test
    public void testUpdateUser() {
        User user = new User(1, "test@example.com", "John", "Doe");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(user);

        assertEquals(userDto, result);
    }

    @Test
    public void testUpdateUserButUserNotFound() {
        User user = new User(1, "test@example.com", "John", "Doe");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(user);
        });

        assertEquals("User was not found", exception.getMessage());
    }

    @Test
    public void testDeleteUser() {
        User user = new User(1, "test@example.com", "John", "Doe");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository).deleteById(1);
    }

    @Test
    public void testDeleteUserButUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(1);
        });

        assertEquals("User was not found", exception.getMessage());
    }

    @Test
    public void testCompareHeaderIdWithRequestedDataIdSuccess() {
        userService.compareHeaderIdWithRequestedDataId(1, "1");
    }

    @Test
    public void testCompareHeaderIdWithRequestedDataIdId() {
        IdMismatchException exception = assertThrows(IdMismatchException.class, () -> {
            // wrong headerUserId
            userService.compareHeaderIdWithRequestedDataId(1, "2");
        });

        assertEquals(IdMismatchException.class, exception.getClass());
    }

}
