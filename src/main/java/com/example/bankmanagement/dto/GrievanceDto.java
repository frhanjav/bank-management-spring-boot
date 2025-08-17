package com.example.bankmanagement.dto;

import com.example.bankmanagement.model.RequestStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GrievanceDto {
    private Long id;
    private String subject;
    private String description;
    private RequestStatus status;
    private LocalDateTime submittedDate;
    private LocalDateTime resolvedDate;
    private Long customerId;
    private String customerName;
    private String handledByUsername;
}