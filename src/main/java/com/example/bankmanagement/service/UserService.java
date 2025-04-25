package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.CreateUserRequest;
import com.example.bankmanagement.dto.UserDto;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    UserDto createUser(CreateUserRequest request);
    User findUserByUsername(String username) throws UsernameNotFoundException;
    User findUserById(Long id) throws ResourceNotFoundException;
}