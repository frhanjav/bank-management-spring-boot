package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.RequestStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FixedDepositDto {
    private Long id;
    private BigDecimal principalAmount;
    private Double interestRate;
    private Integer termMonths;
    private LocalDate maturityDate;
    private RequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private Long customerId;
    private String customerName;
}