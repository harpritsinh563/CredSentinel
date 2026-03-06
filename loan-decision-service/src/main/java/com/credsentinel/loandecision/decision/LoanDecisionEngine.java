package com.credsentinel.loandecision.decision;

import com.credsentinel.loandecision.entity.LoanStatusHistory;
import com.credsentinel.loandecision.entity.RiskScore;
import com.credsentinel.loandecision.entity.User;
import com.credsentinel.loandecision.model.LoanDecisionMadeEvent;
import com.credsentinel.loandecision.model.LoanRequestCreatedEvent;
import com.credsentinel.loandecision.repository.LoanStatusHistoryRepository;
import com.credsentinel.loandecision.repository.RiskScoreRepository;
import com.credsentinel.loandecision.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.credsentinel.loandecision.constant.KycStatus.VERIFIED;
import static com.credsentinel.loandecision.constant.LoanDecisionStatus.APPROVED;
import static com.credsentinel.loandecision.constant.LoanDecisionStatus.REJECTED;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanDecisionEngine {

    private final UserRepository userRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final LoanStatusHistoryRepository statusHistoryRepository;
    private final KafkaTemplate<String, LoanDecisionMadeEvent> kafkaTemplate;

    // TODO : In future, externalize the rules and their evaluators, maintain a RuleRegistry, an interface RuleEvaluator and iterate on the implementations/beans of RuleEvalutors
    @Transactional
    public void process(LoanRequestCreatedEvent event) {
        log.info("Starting decision process for Loan ID: {}", event.getLoanRequestId());

        // 1. Fetch User
        User user = userRepository.findById(UUID.fromString(event.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String decision = APPROVED.toString();
        String reason = "All policy checks passed.";

        // 2. Business Rule: KYC Check
        if (!VERIFIED.toString().equals(user.getKycStatus())) {
            decision = REJECTED.toString();
            reason = "User KYC is not verified.";
        }
        // 3. Business Rule: High Amount Threshold
        else if (event.getAmount().compareTo(new BigDecimal("50000")) > 0 && event.getRiskScore() < 600) {
            decision = REJECTED.toString();
            reason = "High amount requested with insufficient risk score.";
        }

        // 4. Persist the Risk Score (Serious Business Auditing)
        saveRiskData(event, decision);

        // 5. Log to Loan Status History
        saveStatusHistory(event, decision, reason);

        // 6. Final Step: Publish to Kafka
        publishDecision(event, decision, reason);
    }

    private void saveRiskData(LoanRequestCreatedEvent event, String decision) {
        RiskScore risk = new RiskScore();
        risk.setLoanRequestId(UUID.fromString(event.getLoanRequestId()));
        risk.setCreditScore(event.getRiskScore());
        risk.setFinalRiskScore(event.getRiskScore()); // Logic to combine with Anomaly score later
        riskScoreRepository.save(risk);
    }

    private void saveStatusHistory(LoanRequestCreatedEvent event, String decision, String reason) {
        LoanStatusHistory history = new LoanStatusHistory();
        history.setLoanRequestId(UUID.fromString(event.getLoanRequestId()));
        history.setStatus(decision.equals(APPROVED.toString()) ?APPROVED.toString(): REJECTED.toString());
        history.setReason(reason);
        history.setSourceService("loan-decision-service");
        statusHistoryRepository.save(history);
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
    }
}
