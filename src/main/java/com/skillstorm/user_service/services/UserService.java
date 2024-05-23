package com.skillstorm.user_service.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.skillstorm.user_service.RestClients.RestClientRequester;
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

    private RestClientRequester restClient = new RestClientRequester();


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

        // Id must be null to create a new entry
        user.setId(null);

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

        // Make sure that the user with the given ID actually exists
        Optional<User> foundUser = userRepo.findById(id);

        if (foundUser.isEmpty()) {
            throw new ResourceNotFoundException("User");
        }

        // Start sending requests to all of the other services to delete all of the user's information
        ResponseEntity<Void> accountsResponse = restClient.sendRequest("http://accounts/" + id);

        if (accountsResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RuntimeException("Accounts belonging to user could not be deleted");
        }

        ResponseEntity<Void> transactionsResponse = restClient.sendRequest("http://deleteTransaction/user/" + id);

        if (transactionsResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RuntimeException("Transactions belonging to user could not be deleted");
        }

        ResponseEntity<Void> bucketsResponse = restClient.sendRequest("http://buckets/deleteAll/user/" + id);

        ResponseEntity<Void> budgetsResponse = restClient.sendRequest("http://budgets/deleteAll/" + id);

        ResponseEntity<Void> monthlySummaryResponse = restClient.sendRequest("http://summarys/deleteAll/" + id);

        if (bucketsResponse.getStatusCode() != HttpStatus.OK || budgetsResponse.getStatusCode() != HttpStatus.NO_CONTENT || monthlySummaryResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RuntimeException("Budgets belonging to user could not be deleted");
        }

        // TODO: Still missing the URI for the http request to the tax microservice

        // ResponseEntity<Void> taxesResponse = restClient
        //     .delete()
        //     .uri()
        //     .retrieve()
        //     .toBodilessEntity();

        // if (taxesResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
        //     throw new RuntimeException("Budgets belonging to user could not be deleted");
        // }

        userRepo.deleteById(id);
    }
}
