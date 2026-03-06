package com.credsentinel.loanrequestmanagement.listener;

import com.credsentinel.common.enums.LoanStatus;
import com.credsentinel.common.model.LoanDecisionMadeEvent;
import com.credsentinel.loanrequestmanagement.entities.LoanRequest;
import com.credsentinel.loanrequestmanagement.entities.LoanStatusHistory;
import com.credsentinel.loanrequestmanagement.repository.LoanRequestRepository;
import com.credsentinel.loanrequestmanagement.repository.LoanStatusHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoanDecisionEventListener {

    private final LoanRequestRepository loanRequestRepository;
    private final LoanStatusHistoryRepository loanStatusHistoryRepository;

    @KafkaListener(
            topics = "CredSentinel.LoanDecisionMade",
            groupId = "loan-request-service-group"
    )
    @Transactional
    public void onDecisionMade(LoanDecisionMadeEvent event) {
        log.info("Received decision for Loan ID: {}. Result: {}",
                event.getLoanRequestId(), event.getDecision());

        // 1. Find the original request
        LoanRequest loanRequest = loanRequestRepository.findById(UUID.fromString(event.getLoanRequestId()))
                .orElseThrow(() -> new RuntimeException("Loan Request not found: " + event.getLoanRequestId()));

        // 2. Update the status based on the decision
        loanRequest.setCurrentStatus(LoanStatus.valueOf(event.getReason()));
        loanRequestRepository.save(loanRequest);

        // 3. Log the history
        LoanStatusHistory history = new LoanStatusHistory();
        history.setLoanRequestId(loanRequest.getLoanRequestId());
        history.setStatus(event.getDecision());
        history.setReason(event.getReason());
        history.setSourceService("loan-decision-service"); // Log who made the call
        loanStatusHistoryRepository.save(history);

        log.info("Successfully updated Loan ID: {} to status: {}",
                event.getLoanRequestId(), event.getDecision());
    }

}
