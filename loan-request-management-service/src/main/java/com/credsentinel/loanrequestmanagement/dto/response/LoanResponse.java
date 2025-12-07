package com.credsentinel.loanrequestmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LoanResponse {
    private UUID loanRequestId;
    private UUID userId;
    private BigDecimal loanAmount;
    private Integer tenureDays;
    private String sourceChannel;
    private String currentStatus;
    private Instant createdAt;
}
