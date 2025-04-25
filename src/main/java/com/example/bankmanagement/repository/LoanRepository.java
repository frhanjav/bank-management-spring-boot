package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomer(Customer customer);
    List<Loan> findByCustomerId(Long customerId);
    List<Loan> findByStatus(RequestStatus status);
}