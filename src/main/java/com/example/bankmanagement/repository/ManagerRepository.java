package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Manager;
import com.example.bankmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUser(User user);
    Optional<Manager> findByUserId(Long userId);
}