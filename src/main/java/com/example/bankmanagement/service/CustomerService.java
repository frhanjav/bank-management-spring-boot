package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.CustomerDto;
import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.model.User;
import java.util.List;

public interface CustomerService {
    Customer findCustomerByUser(User user);
    Customer findCustomerById(Long customerId);
    CustomerDto getCustomerDetails(String username);
    List<CustomerDto> getAllCustomers();
    void requestProfileUpdate(String username, CustomerDto updateRequest);
}