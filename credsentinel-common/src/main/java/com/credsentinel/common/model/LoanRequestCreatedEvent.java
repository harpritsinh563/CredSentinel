package com.credsentinel.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestCreatedEvent {
    private String loanRequestId;
    private String userId;
    private BigDecimal amount;
    private Integer tenureDays;
    private Integer riskScore;
    private Instant createdAt;
}
