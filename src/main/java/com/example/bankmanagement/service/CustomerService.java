package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.CustomerDto;
import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.model.User;
import java.util.List;

public interface CustomerService {
    Customer findCustomerByUser(User user);
    Customer findCustomerById(Long customerId);
    CustomerDto getCustomerDetails(String username);
    List<CustomerDto> getAllCustomers(); // For staff/manager view
    void requestProfileUpdate(String username, CustomerDto updateRequest); // Customer initiates
    // Manager/Staff might need methods to view/approve updates
}