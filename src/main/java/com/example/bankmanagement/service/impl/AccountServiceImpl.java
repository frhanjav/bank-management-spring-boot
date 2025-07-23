package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.exception.AccountNotFoundException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.mapper.AccountMapper;
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.CustomerRepository;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;


    @Override
    @Transactional
    public AccountDto openAccount(Long customerId, AccountType accountType, Long staffUserId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        String newAccountNumber;
        do {
            newAccountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(newAccountNumber));


        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(accountType);
        account.setAccountNumber(newAccountNumber);
        account.setStatus(AccountStatus.PENDING_APPROVAL);

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        return accountMapper.toDto(account);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getCustomerAccounts(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return accountRepository.findByCustomerIdAndStatus(customerId, AccountStatus.ACTIVE).stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByStatus(status).stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }
}