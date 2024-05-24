package com.skillstorm.user_service.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.exceptions.ExistingAccountException;
import com.skillstorm.user_service.exceptions.ResourceNotFoundException;
import com.skillstorm.user_service.mappers.UserMapper;
import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserMapper mapper;


    public List<UserDto> findAllUsers() {

        List<User> foundUsers = userRepo.findAll();

        List<UserDto> dtos = foundUsers.stream().map(mapper::toDto).collect(Collectors.toList());
        
        return dtos;
    }

    public UserDto findById(int id) {
        
        Optional<User> foundUser = userRepo.findById(id);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User");
        }
        return mapper.toDto(foundUser.get());
    }

    public UserDto createUser(User authUser) {

        // ID value comes from the Authentication service as that's where new users register
        // Don't need to perform a unique check for email (username) as that is also done by the Auth service
        // This creates a User record with an ID and email and all other fields null
        User savedUser = userRepo.save(authUser);

        return mapper.toDto(savedUser);
    }

    // Updates the entire user entry so all fields must be included in the user object
    public UserDto updateUser(User user) {

        Optional<User> foundUser = userRepo.findByEmail(user.getEmail());

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User");
        }

        User updatedUser = userRepo.save(user);

        return mapper.toDto(updatedUser);
    }

    public void deleteUser(int id) {

        Optional<User> foundUser = userRepo.findById(id);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User");
        }

        userRepo.deleteById(id);
    }
}
