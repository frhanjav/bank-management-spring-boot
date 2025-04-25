package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Staff;
import com.example.bankmanagement.model.User;

public interface StaffService {
    Staff findStaffByUser(User user);
}
