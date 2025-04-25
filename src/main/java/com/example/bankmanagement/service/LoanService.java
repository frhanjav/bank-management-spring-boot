package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.LoanDto;
import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    LoanDto applyForLoan(Long customerId, BigDecimal amount, Double interestRate, Integer termMonths);
    List<LoanDto> getCustomerLoans(Long customerId);
    List<LoanDto> getLoansByStatus(com.example.bankmanagement.model.RequestStatus status);
    LoanDto getLoanById(Long loanId);
}