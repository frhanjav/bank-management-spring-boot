package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.AccountDto; // Import AccountDto
import com.example.bankmanagement.dto.CreateUserRequest; // Import CreateUserRequest
import com.example.bankmanagement.model.AccountType; // Import AccountType
import com.example.bankmanagement.model.Staff;
import com.example.bankmanagement.model.User;

public interface StaffService {
    Staff findStaffByUser(User user);

    AccountDto createCustomerAndOpenAccount(CreateUserRequest customerRequest,
                                            AccountType accountType,
                                            Long staffUserId);
}