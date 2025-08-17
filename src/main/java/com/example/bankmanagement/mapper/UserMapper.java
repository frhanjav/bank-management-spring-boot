package com.example.bankmanagement.mapper;

import com.example.bankmanagement.dto.UserDto;
import com.example.bankmanagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        return dto;
    }
}