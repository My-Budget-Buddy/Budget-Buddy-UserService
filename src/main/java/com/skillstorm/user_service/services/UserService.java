package com.skillstorm.user_service.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder encoder;


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

    public UserDto createUser(User user) {

        Optional<User> foundUser = userRepo.findByEmail(user.getEmail());

        if (foundUser.isPresent()) {
            throw new ExistingAccountException();
        }

        // Id must be null to create a new entry. Role must be ROLE_USER
        user.setId(null);
        user.setRole("ROLE_USER");
        // user.setPassword(encoder.encode(user.getPassword()));

        User savedUser = userRepo.save(user);

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
