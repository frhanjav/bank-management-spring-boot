package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.AccountStatus;
import com.example.bankmanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomer(Customer customer);
    List<Account> findByStatus(AccountStatus status);
    List<Account> findByCustomerIdAndStatus(Long customerId, AccountStatus status);
    boolean existsByAccountNumber(String accountNumber);
}
