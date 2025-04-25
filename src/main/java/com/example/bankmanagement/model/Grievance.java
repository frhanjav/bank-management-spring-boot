package com.example.bankmanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Grievance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Lob // Large object for potentially long descriptions
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING; // PENDING = Open, APPROVED = Resolved

    private LocalDateTime submittedDate = LocalDateTime.now();
    private LocalDateTime resolvedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Staff or Manager who handled/resolved it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_user_id") // Link to User ID for flexibility
    private User handledBy;
}