package com.credsentinel.loandecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDecisionMadeEvent {
    private String loanRequestId;
    private String userId;
    private String decision; // APPROVED | REJECTED
    private String reason;
    private Instant decidedAt;
}
