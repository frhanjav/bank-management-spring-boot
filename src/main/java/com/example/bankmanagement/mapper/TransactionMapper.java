package com.example.bankmanagement.mapper;

import com.example.bankmanagement.dto.TransactionDto;
import com.example.bankmanagement.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionDto toDto(Transaction transaction) {
        if (transaction == null) return null;
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setDescription(transaction.getDescription());
        if (transaction.getAccount() != null) {
            dto.setAccountNumber(transaction.getAccount().getAccountNumber());
        }
        return dto;
    }
}
