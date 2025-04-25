package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.FixedDepositDto;
import java.math.BigDecimal;
import java.util.List;

public interface FixedDepositService {
    FixedDepositDto applyForFixedDeposit(Long customerId, BigDecimal amount, Double interestRate, Integer termMonths);
    List<FixedDepositDto> getCustomerFixedDeposits(Long customerId);
    List<FixedDepositDto> getFixedDepositsByStatus(com.example.bankmanagement.model.RequestStatus status);
    FixedDepositDto getFixedDepositById(Long fdId);
}