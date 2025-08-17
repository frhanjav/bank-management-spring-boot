package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.GrievanceDto;
import java.util.List;

public interface GrievanceService {
    GrievanceDto fileGrievance(Long customerId, String subject, String description);
    List<GrievanceDto> getCustomerGrievances(Long customerId);
    List<GrievanceDto> getPendingGrievances();
    GrievanceDto resolveGrievance(Long grievanceId, Long handlerUserId);
    GrievanceDto getGrievanceById(Long grievanceId);

    List<GrievanceDto> getAllGrievances();
}