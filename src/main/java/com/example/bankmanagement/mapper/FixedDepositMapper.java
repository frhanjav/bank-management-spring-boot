package com.example.bankmanagement.mapper;

import com.example.bankmanagement.dto.FixedDepositDto;
import com.example.bankmanagement.model.FixedDeposit;
import org.springframework.stereotype.Component;

@Component
public class FixedDepositMapper {
    public FixedDepositDto toDto(FixedDeposit fd) {
        if (fd == null) return null;
        FixedDepositDto dto = new FixedDepositDto();
        dto.setId(fd.getId());
        dto.setPrincipalAmount(fd.getPrincipalAmount());
        dto.setInterestRate(fd.getInterestRate());
        dto.setTermMonths(fd.getTermMonths());
        dto.setMaturityDate(fd.getMaturityDate());
        dto.setStatus(fd.getStatus());
        dto.setRequestedAt(fd.getRequestedAt());
        dto.setApprovedAt(fd.getApprovedAt());
        if (fd.getCustomer() != null) {
            dto.setCustomerId(fd.getCustomer().getId());
            dto.setCustomerName(fd.getCustomer().getName());
        }
        return dto;
    }
}
