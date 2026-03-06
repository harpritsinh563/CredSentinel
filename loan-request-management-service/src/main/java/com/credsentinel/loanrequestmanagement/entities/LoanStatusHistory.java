package com.credsentinel.loanrequestmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_status_history")
@Data
public class LoanStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID statusId;

    private UUID loanRequestId;
    private String status; // 'APPROVED', 'REJECTED', etc.
    private String reason;
    private String sourceService; // Set to "loan-decision-service"

    @Column(insertable = false, updatable = false)
    private Instant timestamp;
}
