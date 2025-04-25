package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private boolean enabled;
}