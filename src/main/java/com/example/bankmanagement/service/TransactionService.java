package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.TransactionDto;
import com.example.bankmanagement.dto.TransactionRequestDto;
import java.util.List;

public interface TransactionService {
    TransactionDto performDeposit(TransactionRequestDto request, Long staffUserId);
    TransactionDto performWithdrawal(TransactionRequestDto request, Long staffUserId);
    List<TransactionDto> getAccountTransactions(String accountNumber);
}