package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.dto.CreateUserRequest;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.mapper.AccountMapper; // Import AccountMapper
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.*;
import com.example.bankmanagement.service.StaffService;
import com.example.bankmanagement.util.AccountNumberGenerator; // Import generator
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.model.Staff;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.StaffRepository;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository; // Inject UserRepository
    private final CustomerRepository customerRepository; // Inject CustomerRepository
    private final AccountRepository accountRepository; // Inject AccountRepository
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    private final AccountNumberGenerator accountNumberGenerator; // Inject Generator
    private final AccountMapper accountMapper; // Inject AccountMapper

    @Override
    public Staff findStaffByUser(User user) {
        return staffRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found for user: " + user.getUsername()));
    }

    @Override
    @Transactional // Ensure atomicity for all creation steps
    public AccountDto createCustomerAndOpenAccount(CreateUserRequest customerRequest,
                                                   AccountType accountType,
                                                   Long staffUserId) {

        // --- Validation ---
        if (customerRequest.getEmail() == null || customerRequest.getEmail().isBlank() ||
                customerRequest.getPhone() == null || customerRequest.getPhone().isBlank() ||
                customerRequest.getAddress() == null || customerRequest.getAddress().isBlank() ||
                customerRequest.getName() == null || customerRequest.getName().isBlank() ||
                customerRequest.getUsername() == null || customerRequest.getUsername().isBlank() ||
                customerRequest.getPassword() == null || customerRequest.getPassword().isBlank()) {
            throw new InvalidRequestException("Name, Email, Phone, Address, Username, and Password are required for Customer creation.");
        }
        if (userRepository.existsByUsername(customerRequest.getUsername())) {
            throw new InvalidRequestException("Username already exists: " + customerRequest.getUsername());
        }
        // Verify the staff user exists (optional but good practice)
        staffRepository.findByUserId(staffUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Performing Staff user not found with ID: " + staffUserId));

        // --- 1. Create User (Disabled by default) ---
        User user = new User();
        user.setUsername(customerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(customerRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(false); // Customer login disabled until account approval
        User savedUser = userRepository.save(user);

        // --- 2. Create Customer Profile (Inactive by default) ---
        Customer customer = new Customer();
        customer.setName(customerRequest.getName());
        customer.setEmail(customerRequest.getEmail());
        customer.setPhone(customerRequest.getPhone());
        customer.setAddress(customerRequest.getAddress());
        customer.setPanNumber(customerRequest.getPanNumber()); // Optional
        customer.setUser(savedUser); // Link to the User entity
        customer.setActive(false); // Customer profile inactive until account approval
        Customer savedCustomer = customerRepository.save(customer);

        // --- 3. Create Initial Account (Pending Approval) ---
        String newAccountNumber;
        do {
            newAccountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(newAccountNumber));

        Account account = new Account();
        account.setCustomer(savedCustomer); // Link to the Customer entity
        account.setAccountType(accountType);
        account.setAccountNumber(newAccountNumber);
        account.setStatus(AccountStatus.PENDING_APPROVAL); // Requires manager approval
        // Balance starts at ZERO, createdAt is set automatically

        Account savedAccount = accountRepository.save(account);

        // Return DTO of the created (pending) account
        return accountMapper.toDto(savedAccount);
    }
}