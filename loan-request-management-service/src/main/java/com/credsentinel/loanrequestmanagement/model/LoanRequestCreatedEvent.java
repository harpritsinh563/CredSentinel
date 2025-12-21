package com.credsentinel.loanrequestmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanRequestCreatedEvent {
    private String loanRequestId;
    private String userId;
    private BigDecimal amount;
    private Integer tenureDays;
    private Integer riskScore;
    private Instant createdAt;
}
