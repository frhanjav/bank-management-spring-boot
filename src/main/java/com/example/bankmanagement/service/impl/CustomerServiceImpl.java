package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.CustomerDto;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.mapper.CustomerMapper;
import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.CustomerRepository;
import com.example.bankmanagement.repository.UserRepository;
import com.example.bankmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Customer findCustomerByUser(User user) {
        return customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user: " + user.getUsername()));
    }

    @Override
    public Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
    }


    @Override
    @Transactional(readOnly = true)
    public CustomerDto getCustomerDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Customer customer = findCustomerByUser(user);
        return customerMapper.toDto(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void requestProfileUpdate(String username, CustomerDto updateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Customer customer = findCustomerByUser(user);

        if (updateRequest.getPanNumber() != null && !updateRequest.getPanNumber().isBlank()) {
            customer.setPanNumber(updateRequest.getPanNumber());
        }
        if (updateRequest.getAddress() != null && !updateRequest.getAddress().isBlank()) {
            customer.setAddress(updateRequest.getAddress());
        }
        customerRepository.save(customer);
        System.out.println("Profile update requested for " + username + ". Needs approval.");
    }
}