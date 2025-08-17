package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.AccountStatus;
import com.example.bankmanagement.model.AccountType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountStatus status;
    private AccountType accountType;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private Long customerId;
    private String customerName;
}