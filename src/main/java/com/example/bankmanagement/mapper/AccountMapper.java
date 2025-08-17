package com.example.bankmanagement.mapper;

import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public AccountDto toDto(Account account) {
        if (account == null) return null;
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        dto.setAccountType(account.getAccountType());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setApprovedAt(account.getApprovedAt());
        if (account.getCustomer() != null) {
            dto.setCustomerId(account.getCustomer().getId());
            dto.setCustomerName(account.getCustomer().getName());
        }
        return dto;
    }
}