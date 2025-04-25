package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.GrievanceDto;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.exception.UnauthorizedOperationException;
import com.example.bankmanagement.mapper.GrievanceMapper;
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.CustomerRepository;
import com.example.bankmanagement.repository.GrievanceRepository;
import com.example.bankmanagement.repository.UserRepository;
import com.example.bankmanagement.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrievanceServiceImpl implements GrievanceService {

    private final GrievanceRepository grievanceRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final GrievanceMapper grievanceMapper;

    @Override
    @Transactional
    public GrievanceDto fileGrievance(Long customerId, String subject, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        // Crucial Check: Only active customers can file
        if (!customer.isActive()) {
            throw new UnauthorizedOperationException("Customer account is not active. Cannot file grievance.");
        }
        if (subject == null || subject.isBlank() || description == null || description.isBlank()) {
            throw new InvalidRequestException("Subject and description cannot be empty.");
        }

        Grievance grievance = new Grievance();
        grievance.setCustomer(customer);
        grievance.setSubject(subject);
        grievance.setDescription(description);
        grievance.setStatus(RequestStatus.PENDING); // PENDING = Open
        // submittedDate is set automatically

        Grievance savedGrievance = grievanceRepository.save(grievance);
        return grievanceMapper.toDto(savedGrievance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrievanceDto> getCustomerGrievances(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return grievanceRepository.findByCustomerId(customerId).stream()
                .map(grievanceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrievanceDto> getPendingGrievances() {
        // PENDING status means open/unresolved
        return grievanceRepository.findByStatus(RequestStatus.PENDING).stream()
                .map(grievanceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GrievanceDto resolveGrievance(Long grievanceId, Long handlerUserId) {
        Grievance grievance = grievanceRepository.findById(grievanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found: " + grievanceId));

        User handler = userRepository.findById(handlerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Handling user not found: " + handlerUserId));

        // Ensure handler is Staff or Manager
        if (handler.getRole() != Role.STAFF && handler.getRole() != Role.MANAGER) {
            throw new UnauthorizedOperationException("Only Staff or Manager can resolve grievances.");
        }

        if (grievance.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("Grievance is not currently open (pending).");
        }

        grievance.setStatus(RequestStatus.APPROVED); // Using APPROVED to mean Resolved
        grievance.setResolvedDate(LocalDateTime.now());
        grievance.setHandledBy(handler);

        Grievance savedGrievance = grievanceRepository.save(grievance);
        return grievanceMapper.toDto(savedGrievance);
    }

    @Override
    @Transactional(readOnly = true)
    public GrievanceDto getGrievanceById(Long grievanceId) {
        Grievance grievance = grievanceRepository.findById(grievanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found with ID: " + grievanceId));
        return grievanceMapper.toDto(grievance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrievanceDto> getAllGrievances() {
        // Fetch all grievances and map them to DTOs
        return grievanceRepository.findAll().stream()
                .map(grievanceMapper::toDto)
                .collect(Collectors.toList());
    }
}