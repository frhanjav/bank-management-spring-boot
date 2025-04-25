package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.model.Staff;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.StaffRepository;
import com.example.bankmanagement.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    @Override
    public Staff findStaffByUser(User user) {
        return staffRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found for user: " + user.getUsername()));
    }
}