package com.credsentinel.loanrequestmanagement.strategy.impl;

import com.credsentinel.loanrequestmanagement.dto.request.LoanCreateRequest;
import com.credsentinel.loanrequestmanagement.entities.LoanRequest;
import com.credsentinel.loanrequestmanagement.repository.LoanRequestRepository;
import com.credsentinel.loanrequestmanagement.strategy.LoanOperationStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.credsentinel.loanrequestmanagement.constants.LoanOperation;
import com.credsentinel.loanrequestmanagement.dto.response.LoanResponse;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateLoanOperationStrategy implements LoanOperationStrategy {

    private final LoanRequestRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(LoanOperation loanOperation) {
        return loanOperation.equals(LoanOperation.CREATE);
    }

    @Override
    public Object execute(Object request) {
        LoanCreateRequest req = (LoanCreateRequest) request;

        LoanRequest entity = LoanRequest.builder()
                .loanRequestId(UuidCreator.getTimeOrdered())
                .userId(UUID.fromString(req.getUserId()))
                .loanAmount(req.getLoanAmount())
                .tenureDays(req.getTenureDays())
                .sourceChannel(req.getSourceChannel())
                .requestPayload(serializePayload(req))
                .currentStatus(LoanStatus.REQUESTED)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(86400))
                .build();


        // save loan_request
        LoanRequest saved = repository.save(entity);

        // Placeholder : Publish Kafka event

        return mapToResponse(saved);
    }

    private String serializePayload(LoanCreateRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getRequestPayload());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize request payload", ex);
        }
    }

    private LoanResponse mapToResponse(LoanRequest entity) {
        return LoanResponse.builder()
                .loanRequestId(entity.getLoanRequestId())
                .userId(entity.getUserId())
                .loanAmount(entity.getLoanAmount())
                .tenureDays(entity.getTenureDays())
                .sourceChannel(entity.getSourceChannel())
                .currentStatus(entity.getCurrentStatus().name())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
