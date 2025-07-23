package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.dto.CreateUserRequest;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.mapper.AccountMapper;
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.*;
import com.example.bankmanagement.service.StaffService;
import com.example.bankmanagement.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankmanagement.model.Staff;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.StaffRepository;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AccountMapper accountMapper;

    @Override
    public Staff findStaffByUser(User user) {
        return staffRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found for user: " + user.getUsername()));
    }

    @Override
    @Transactional
    public AccountDto createCustomerAndOpenAccount(CreateUserRequest customerRequest,
                                                   AccountType accountType,
                                                   Long staffUserId) {

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
        staffRepository.findByUserId(staffUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Performing Staff user not found with ID: " + staffUserId));

        User user = new User();
        user.setUsername(customerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(customerRequest.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(false);
        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setName(customerRequest.getName());
        customer.setEmail(customerRequest.getEmail());
        customer.setPhone(customerRequest.getPhone());
        customer.setAddress(customerRequest.getAddress());
        customer.setPanNumber(customerRequest.getPanNumber());
        customer.setUser(savedUser);
        customer.setActive(false);
        Customer savedCustomer = customerRepository.save(customer);

        String newAccountNumber;
        do {
            newAccountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(newAccountNumber));

        Account account = new Account();
        account.setCustomer(savedCustomer);
        account.setAccountType(accountType);
        account.setAccountNumber(newAccountNumber);
        account.setStatus(AccountStatus.PENDING_APPROVAL);

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }
}