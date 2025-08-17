package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.TransactionDto;
import com.example.bankmanagement.dto.TransactionRequestDto;
import com.example.bankmanagement.exception.AccountNotFoundException;
import com.example.bankmanagement.exception.InsufficientBalanceException;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.mapper.TransactionMapper;
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.StaffRepository;
import com.example.bankmanagement.repository.TransactionRepository;
import com.example.bankmanagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final StaffRepository staffRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionDto performDeposit(TransactionRequestDto request, Long staffUserId) {
        validateTransactionRequest(request);
        Staff performingStaff = staffRepository.findByUserId(staffUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found with ID: " + staffUserId));

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.getAccountNumber()));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidRequestException("Account is not active. Cannot perform transactions.");
        }

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = createTransaction(request, account, TransactionType.DEPOSIT, performingStaff);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public TransactionDto performWithdrawal(TransactionRequestDto request, Long staffUserId) {
        validateTransactionRequest(request);
        Staff performingStaff = staffRepository.findByUserId(staffUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found with ID: " + staffUserId));

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.getAccountNumber()));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidRequestException("Account is not active. Cannot perform transactions.");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal.");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = createTransaction(request, account, TransactionType.WITHDRAWAL, performingStaff);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getAccountTransactions(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        return transactionRepository.findByAccountOrderByTimestampDesc(account).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateTransactionRequest(TransactionRequestDto request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Transaction amount must be positive.");
        }
        if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            throw new InvalidRequestException("Account number is required.");
        }
    }

    private Transaction createTransaction(TransactionRequestDto request, Account account, TransactionType type, Staff staff) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionType(type);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setPerformedByStaff(staff);
        return transaction;
    }
}