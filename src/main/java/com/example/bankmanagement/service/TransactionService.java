package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.TransactionDto;
import com.example.bankmanagement.dto.TransactionRequestDto;
import java.util.List;

public interface TransactionService {
    TransactionDto performDeposit(TransactionRequestDto request, Long staffUserId); // Staff performs
    TransactionDto performWithdrawal(TransactionRequestDto request, Long staffUserId); // Staff performs
    List<TransactionDto> getAccountTransactions(String accountNumber);
}