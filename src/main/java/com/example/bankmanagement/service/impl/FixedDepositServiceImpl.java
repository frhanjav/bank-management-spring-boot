package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.FixedDepositDto;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.exception.UnauthorizedOperationException;
import com.example.bankmanagement.mapper.FixedDepositMapper;
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.CustomerRepository;
import com.example.bankmanagement.repository.FixedDepositRepository;
import com.example.bankmanagement.service.FixedDepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixedDepositServiceImpl implements FixedDepositService {

    private final FixedDepositRepository fixedDepositRepository;
    private final CustomerRepository customerRepository;
    private final FixedDepositMapper fdMapper;

    @Override
    @Transactional
    public FixedDepositDto applyForFixedDeposit(Long customerId, BigDecimal amount, Double interestRate, Integer termMonths) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        // Crucial Check: Only active customers can apply
        if (!customer.isActive()) {
            throw new UnauthorizedOperationException("Customer account is not active. Cannot apply for Fixed Deposit.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || interestRate == null || interestRate <= 0 || termMonths == null || termMonths <= 0) {
            throw new InvalidRequestException("Invalid Fixed Deposit parameters provided.");
        }

        FixedDeposit fd = new FixedDeposit();
        fd.setCustomer(customer);
        fd.setPrincipalAmount(amount);
        fd.setInterestRate(interestRate); // Rate might be determined by rules/term
        fd.setTermMonths(termMonths);
        fd.setStatus(RequestStatus.PENDING); // Requires manager approval
        // requestedAt is set automatically
        // maturityDate is set upon approval

        FixedDeposit savedFd = fixedDepositRepository.save(fd);
        return fdMapper.toDto(savedFd);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FixedDepositDto> getCustomerFixedDeposits(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return fixedDepositRepository.findByCustomerId(customerId).stream()
                .map(fdMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FixedDepositDto> getFixedDepositsByStatus(RequestStatus status) {
        return fixedDepositRepository.findByStatus(status).stream()
                .map(fdMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FixedDepositDto getFixedDepositById(Long fdId) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found with ID: " + fdId));
        return fdMapper.toDto(fd);
    }
}