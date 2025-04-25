package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.Role;
import lombok.Data;

@Data
public class CreateUserRequest {
    // For Manager creating Staff/Customer
    private String username;
    private String password; // Initial password
    private Role role;
    private String name;
    private String email; // Required for customer
    private String phone; // Required for customer
    private String address; // Required for customer
    private String panNumber; // Optional initially for customer
    private String employeeId; // Required for Staff/Manager
}