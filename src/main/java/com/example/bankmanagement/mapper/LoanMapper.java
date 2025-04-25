package com.example.bankmanagement.mapper;

import com.example.bankmanagement.dto.LoanDto;
import com.example.bankmanagement.model.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {
    public LoanDto toDto(Loan loan) {
        if (loan == null) return null;
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTermMonths(loan.getTermMonths());
        dto.setStatus(loan.getStatus());
        dto.setRequestedAt(loan.getRequestedAt());
        dto.setApprovedAt(loan.getApprovedAt());
        if (loan.getCustomer() != null) {
            dto.setCustomerId(loan.getCustomer().getId());
            dto.setCustomerName(loan.getCustomer().getName());
        }
        return dto;
    }
}
