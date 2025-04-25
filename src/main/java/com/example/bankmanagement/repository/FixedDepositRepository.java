package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.model.FixedDeposit;
import com.example.bankmanagement.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
    List<FixedDeposit> findByCustomer(Customer customer);
    List<FixedDeposit> findByCustomerId(Long customerId);
    List<FixedDeposit> findByStatus(RequestStatus status);
}