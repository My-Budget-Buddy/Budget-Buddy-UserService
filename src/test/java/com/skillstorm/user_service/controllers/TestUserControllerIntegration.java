package com.skillstorm.user_service.controllers;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.services.UserService;

@WebMvcTest(UserController.class)
public class TestUserControllerIntegration {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFindAllUsers_Success() throws Exception {
        UserDto user1 = new UserDto(1, "email1@example.com", "John", "Doe");
        UserDto user2 = new UserDto(2, "email2@example.com", "Jane", "Doe");

        when(userService.findAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("email1@example.com"))
                .andExpect(jsonPath("$[1].email").value("email2@example.com"));
    }

    @Test
    public void testFindUserById_Success() throws Exception {
        UserDto user = new UserDto(1, "email@example.com", "John", "Doe");

        when(userService.findById(Mockito.anyInt())).thenReturn(user);

        mockMvc.perform(get("/users/user")
                        .header("User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("email@example.com"));
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        User user = new User(1, "email@example.com", "John", "Doe");
        UserDto userDto = new UserDto(1, "email@example.com", "John", "Doe");

        when(userService.createUser(Mockito.any(User.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("email@example.com"));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        User updatedUser = new User(1, "email@example.com", "Jane", "Doe");
        UserDto updatedUserDto = new UserDto(1, "email@example.com", "Jane", "Doe");

        when(userService.updateUser(Mockito.any(User.class))).thenReturn(updatedUserDto);
        Mockito.doNothing().when(userService).compareHeaderIdWithRequestedDataId(Mockito.anyInt(), Mockito.anyString());

        mockMvc.perform(put("/users")
                        .header("User-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        // Use doNothing for void methods
        Mockito.doNothing().when(userService).deleteUser(Mockito.anyInt());

        mockMvc.perform(delete("/users")
                        .header("User-ID", "1"))
                .andExpect(status().isOk());
    }
}
