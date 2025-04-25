package com.example.bankmanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String panNumber;
    private boolean active;
    private UserDto user; // Include basic user info if needed
    private List<AccountDto> accounts; // Optional: include accounts in detailed view
}