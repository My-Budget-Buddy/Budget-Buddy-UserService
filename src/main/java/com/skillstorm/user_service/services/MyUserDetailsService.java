package com.skillstorm.user_service.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.skillstorm.user_service.models.User;
import com.skillstorm.user_service.repositories.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> foundAcct = userRepo.findByEmail(email);
        
        if (foundAcct.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        return foundAcct.get();
    }
    
}
