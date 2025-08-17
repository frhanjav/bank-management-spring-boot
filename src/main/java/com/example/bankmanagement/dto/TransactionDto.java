package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String description;
    private String accountNumber;
}

