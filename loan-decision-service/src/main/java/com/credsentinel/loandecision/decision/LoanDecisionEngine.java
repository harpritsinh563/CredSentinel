package com.credsentinel.loandecision.decision;

import com.credsentinel.loandecision.entity.LoanStatusHistory;
import com.credsentinel.loandecision.entity.RiskScore;
import com.credsentinel.loandecision.entity.User;
import com.credsentinel.common.model.LoanDecisionMadeEvent;
import com.credsentinel.common.model.LoanRequestCreatedEvent;
import com.credsentinel.loandecision.repository.LoanStatusHistoryRepository;
import com.credsentinel.loandecision.repository.RiskScoreRepository;
import com.credsentinel.loandecision.repository.UserRepository;
import com.loan.anomaly.grpc.LoanAnomalyRequest;
import com.loan.anomaly.grpc.LoanAnomalyResponse;
import com.loan.anomaly.grpc.LoanAnomalyServiceGrpc;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.credsentinel.loandecision.constant.AnomalyStatus.NORMAL;
import static com.credsentinel.loandecision.constant.AnomalyStatus.SUSPICIOUS;
import static com.credsentinel.loandecision.constant.KycStatus.VERIFIED;
import static com.credsentinel.common.enums.LoanStatus.APPROVED;
import static com.credsentinel.common.enums.LoanStatus.REJECTED;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanDecisionEngine {

    private final UserRepository userRepository;
    private final RiskScoreRepository riskScoreRepository;
    private final LoanStatusHistoryRepository statusHistoryRepository;
    private final KafkaTemplate<String, LoanDecisionMadeEvent> kafkaTemplate;
    @GrpcClient("loan-anomaly-service")
    private LoanAnomalyServiceGrpc.LoanAnomalyServiceBlockingStub anomalyStub;

    @Transactional
    public void process(LoanRequestCreatedEvent event) {
        log.info("Starting decision process for Loan ID: {}", event.getLoanRequestId());

        // 1. Fetch User
        User user = userRepository.findById(UUID.fromString(event.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Call Anomaly Service via gRPC
        String anomalyStatus = getAnomalyStatus(event);
        log.info("Anomaly service returned status: {} for Loan ID: {}", anomalyStatus, event.getLoanRequestId());

        String decision = APPROVED.toString();
        String reason = "All policy and behavioral checks passed.";

        // 3. Business Rule: Behavioral Anomaly Check
        if (SUSPICIOUS.toString().equals(anomalyStatus)) {
            decision = REJECTED.toString();
            reason = "Behavioral anomaly detected by risk engine.";
        }
        // 4. Business Rule: KYC Check
        else if (!VERIFIED.toString().equals(user.getKycStatus())) {
            decision = REJECTED.toString();
            reason = "User KYC is not verified.";
        }
        // 5. Business Rule: High Amount Threshold
        else if (event.getAmount().compareTo(new BigDecimal("50000")) > 0 && event.getRiskScore() < 600) {
            decision = REJECTED.toString();
            reason = "High amount requested with insufficient risk score.";
        }

        // 6. Persist the Risk Score (updated to include anomaly status)
        saveRiskData(event, anomalyStatus, decision);

        // 7. Log to Loan Status History
        saveStatusHistory(event, decision, reason);

        // 8. Final Step: Publish to Kafka
        publishDecision(event, decision, reason);
    }

    private String getAnomalyStatus(LoanRequestCreatedEvent event) {
        try {
            LoanAnomalyRequest request = LoanAnomalyRequest.newBuilder()
                    .setLoanId(event.getLoanRequestId())
                    .setUserId(event.getUserId())
                    .setLoanAmount(event.getAmount().doubleValue())
                    .setTenureMonths(event.getTenureDays() / 30) // Approximation
                    .build();

            LoanAnomalyResponse response = anomalyStub.detectAnomaly(request);
            return response.getStatus();
        } catch (Exception e) {
            log.error("Failed to reach Anomaly Service for Loan ID: {}. Defaulting to NORMAL.", event.getLoanRequestId(), e);
            return NORMAL.toString(); // Fail-safe: allow processing to continue
        }
    }

    private void saveRiskData(LoanRequestCreatedEvent event, String anomalyStatus, String decision) {
        RiskScore risk = new RiskScore();
        risk.setLoanRequestId(UUID.fromString(event.getLoanRequestId()));
        risk.setCreditScore(event.getRiskScore());

        // MVP: Flag anomaly score as 100 if suspicious, 0 otherwise
        risk.setAnomalyScore(SUSPICIOUS.toString().equals(anomalyStatus) ? 100 : 0);
        risk.setFinalRiskScore(event.getRiskScore());

        risk.setFraudFlags(String.format("{\"anomaly_status\": \"%s\"}", anomalyStatus));

        riskScoreRepository.save(risk);
    }

    private void saveStatusHistory(LoanRequestCreatedEvent event, String decision, String reason) {
        LoanStatusHistory history = new LoanStatusHistory();
        history.setLoanRequestId(UUID.fromString(event.getLoanRequestId()));
        history.setStatus(decision.equals(APPROVED.toString()) ? APPROVED.toString() : REJECTED.toString());
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
