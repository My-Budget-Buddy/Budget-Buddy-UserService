package com.skillstorm.user_service.mappers;

import org.springframework.stereotype.Component;

import com.skillstorm.user_service.dtos.UserDto;
import com.skillstorm.user_service.models.User;

@Component
public class UserMapper {
    
    public User toUser(UserDto dto) {
        return new User(dto.getId(), dto.getEmail(), dto.getPassword(), dto.getFirstName(), dto.getLastName()); 
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName());
    }
}
