package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequestDto {
    private String accountNumber;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
}
