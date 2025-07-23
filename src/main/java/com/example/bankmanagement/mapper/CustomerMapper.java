package com.example.bankmanagement.mapper;

import com.example.bankmanagement.dto.CustomerDto;
import com.example.bankmanagement.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    private final UserMapper userMapper;

    public CustomerDto toDto(Customer customer) {
        if (customer == null) return null;
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setPanNumber(customer.getPanNumber());
        dto.setActive(customer.isActive());
        if (customer.getUser() != null) {
            dto.setUser(userMapper.toDto(customer.getUser()));
        }
        return dto;
    }
}