package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Customer;
import com.example.bankmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser(User user);
    Optional<Customer> findByUserId(Long userId); // Convenience method
}
