package com.skillstorm.user_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.services.UserService;

@RestController
@RequestMapping("users")
public class UserController {
    
    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<UserDto>> findAllUsers() {

        List<UserDto> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> findUserById(@RequestHeader(name = "User-ID") String userId) {

        UserDto user = userService.findById(Integer.valueOf(userId));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {

        UserDto createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@RequestBody User user, @RequestHeader(name = "User-ID") String userId) {

        userService.compareHeaderIdWithRequestedDataId(user.getId(), userId);

        UserDto updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(@RequestHeader(name = "User-ID") String userId) {

        userService.deleteUser(Integer.valueOf(userId));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
