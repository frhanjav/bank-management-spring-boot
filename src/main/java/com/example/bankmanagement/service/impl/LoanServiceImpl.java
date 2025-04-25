package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.LoanDto;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.exception.UnauthorizedOperationException;
import com.example.bankmanagement.mapper.LoanMapper;
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.CustomerRepository;
import com.example.bankmanagement.repository.LoanRepository;
import com.example.bankmanagement.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanMapper loanMapper;

    @Override
    @Transactional
    public LoanDto applyForLoan(Long customerId, BigDecimal amount, Double interestRate, Integer termMonths) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        // Crucial Check: Only active customers can apply
        if (!customer.isActive()) {
            throw new UnauthorizedOperationException("Customer account is not active. Cannot apply for loan.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || interestRate == null || interestRate <= 0 || termMonths == null || termMonths <= 0) {
            throw new InvalidRequestException("Invalid loan parameters provided.");
        }


        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(amount);
        loan.setInterestRate(interestRate); // In a real system, rate might be determined by rules
        loan.setTermMonths(termMonths);
        loan.setStatus(RequestStatus.PENDING); // Requires manager approval
        // requestedAt is set automatically

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> getCustomerLoans(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return loanRepository.findByCustomerId(customerId).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> getLoansByStatus(RequestStatus status) {
        return loanRepository.findByStatus(status).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDto getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));
        return loanMapper.toDto(loan);
    }
}