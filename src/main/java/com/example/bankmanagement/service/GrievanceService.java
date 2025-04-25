package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.GrievanceDto;
import java.util.List;

public interface GrievanceService {
    GrievanceDto fileGrievance(Long customerId, String subject, String description);
    List<GrievanceDto> getCustomerGrievances(Long customerId);
    List<GrievanceDto> getPendingGrievances(); // For staff/manager
    GrievanceDto resolveGrievance(Long grievanceId, Long handlerUserId); // Staff/Manager resolves
    GrievanceDto getGrievanceById(Long grievanceId);

    List<GrievanceDto> getAllGrievances(); // For Manager overview
}