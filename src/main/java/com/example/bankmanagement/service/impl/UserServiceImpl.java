package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.CreateUserRequest;
import com.example.bankmanagement.dto.UserDto;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.mapper.UserMapper;
import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.UserRepository;
import com.example.bankmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new InvalidRequestException("Username already exists: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        user.setEnabled(request.getRole() != Role.CUSTOMER);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public User findUserById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}
