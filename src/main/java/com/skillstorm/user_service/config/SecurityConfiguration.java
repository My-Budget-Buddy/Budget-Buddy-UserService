package com.skillstorm.user_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.skillstorm.user_service.services.MyUserDetailsService;

import jakarta.ws.rs.HttpMethod;

@Configuration
public class SecurityConfiguration {

    @Autowired
    MyUserDetailsService userDetailsService;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests((authorizeHttpRequests) -> 
            
                authorizeHttpRequests
                    .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                    .anyRequest().permitAll()
            );
            return http.build();
    }

    @Bean
    protected PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(5);
    }

    // Load encoder and user details so that Spring Security can perform authentication
    @Bean
    protected DaoAuthenticationProvider authenticationProvicer() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());

        return authProvider;
    }

    @Bean
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
}
