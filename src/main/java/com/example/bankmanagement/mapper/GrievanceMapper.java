package com.example.bankmanagement.mapper;

import com.example.bankmanagement.dto.GrievanceDto;
import com.example.bankmanagement.model.Grievance;
import org.springframework.stereotype.Component;

@Component
public class GrievanceMapper {
    public GrievanceDto toDto(Grievance grievance) {
        if (grievance == null) return null;
        GrievanceDto dto = new GrievanceDto();
        dto.setId(grievance.getId());
        dto.setSubject(grievance.getSubject());
        dto.setDescription(grievance.getDescription());
        dto.setStatus(grievance.getStatus());
        dto.setSubmittedDate(grievance.getSubmittedDate());
        dto.setResolvedDate(grievance.getResolvedDate());
        if (grievance.getCustomer() != null) {
            dto.setCustomerId(grievance.getCustomer().getId());
            dto.setCustomerName(grievance.getCustomer().getName());
        }
        if (grievance.getHandledBy() != null) {
            dto.setHandledByUsername(grievance.getHandledBy().getUsername());
        }
        return dto;
    }
}
