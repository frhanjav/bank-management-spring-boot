package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.AccountStatus;
import lombok.Data;

@Data
public class AccountStatusUpdateDto {
    private AccountStatus status;
}