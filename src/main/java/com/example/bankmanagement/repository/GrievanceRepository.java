package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.model.Grievance;
import com.example.bankmanagement.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    List<Grievance> findByCustomer(Customer customer);
    List<Grievance> findByCustomerId(Long customerId);
    List<Grievance> findByStatus(RequestStatus status);
}