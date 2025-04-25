package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.model.AccountType;
import java.util.List;

public interface AccountService {
    AccountDto openAccount(Long customerId, AccountType accountType, Long staffUserId); // Staff initiates
    AccountDto getAccountByNumber(String accountNumber);
    AccountDto getAccountById(Long accountId);
    List<AccountDto> getCustomerAccounts(Long customerId);
    List<AccountDto> getAccountsByStatus(com.example.bankmanagement.model.AccountStatus status);
}