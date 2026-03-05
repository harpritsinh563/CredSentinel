package com.credsentinel.loandecision.decision;

import com.credsentinel.loandecision.model.LoanDecisionMadeEvent;
import com.credsentinel.loandecision.model.LoanRequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

import static com.credsentinel.loandecision.constant.LoanDecisionStatus.APPROVED;
import static com.credsentinel.loandecision.constant.LoanDecisionStatus.REJECTED;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanDecisionEngine {

    private final KafkaTemplate<String, LoanDecisionMadeEvent> kafkaTemplate;

    public void process(LoanRequestCreatedEvent event) {
        String decision = APPROVED.name();
        StringBuilder reason = new StringBuilder("Policy checks passed.");

        // 1. Business Rule: Maximum Amount Check
        if (event.getAmount().compareTo(new BigDecimal("100000")) > 0) {
            decision = REJECTED.name();
            reason = new StringBuilder("Amount exceeds maximum limit for automated approval.");
        }

        // 2. Business Rule: Tenure Check (Must be between 30 and 365 days)
        if (event.getTenureDays() < 30 || event.getTenureDays() > 365) {
            decision = REJECTED.name();
            reason = new StringBuilder("Invalid tenure requested.");
        }

        // 3. Risk Score Check (Initial Logic)
        if (event.getRiskScore() != null && event.getRiskScore() < 500) {
            decision = REJECTED.name();
            reason = new StringBuilder("Risk score below minimum threshold.");
        }

        // Publish the result
        publishDecision(event, decision, reason.toString());
    }

    private void publishDecision(LoanRequestCreatedEvent event, String decision, String reason) {
        LoanDecisionMadeEvent result = LoanDecisionMadeEvent.builder()
                .loanRequestId(event.getLoanRequestId())
                .userId(event.getUserId())
                .decision(decision)
                .reason(reason)
                .decidedAt(Instant.now())
                .build();

        kafkaTemplate.send("CredSentinel.LoanDecisionMade", result.getLoanRequestId(), result);
        log.info("Decision published: {} for Loan: {}", decision, event.getLoanRequestId());
    }
}
