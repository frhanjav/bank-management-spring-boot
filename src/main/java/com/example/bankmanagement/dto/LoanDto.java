package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.RequestStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanDto {
    private Long id;
    private BigDecimal loanAmount;
    private Double interestRate;
    private Integer termMonths;
    private RequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private Long customerId;
    private String customerName;
}
