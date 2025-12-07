package com.credsentinel.loanrequestmanagement.entities;

import com.github.f4b6a3.uuid.UuidCreator;
import com.credsentinel.loanrequestmanagement.constants.LoanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loan_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequestEntity {
    @Id
    @Column(name = "loan_request_id", nullable = false)
    private UUID loanRequestId= UuidCreator.getTimeOrdered();

    @Column(name = "user_id", nullable = false)
    private UUID userId = UuidCreator.getTimeOrdered();

    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;

    @Column(name = "tenure_days", nullable = false)
    private Integer tenureDays;

    @Column(name = "source_channel")
    private String sourceChannel;

    /**
     * Stored as TEXT for H2
     * Seamlessly switches to JSONB in PostgreSQL
     */
    @Lob
    @Column(name = "request_payload", nullable = false)
    private String requestPayload;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private LoanStatus currentStatus;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;
}
