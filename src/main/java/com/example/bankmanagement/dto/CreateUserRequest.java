package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private Role role;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String panNumber;
    private String employeeId;
}