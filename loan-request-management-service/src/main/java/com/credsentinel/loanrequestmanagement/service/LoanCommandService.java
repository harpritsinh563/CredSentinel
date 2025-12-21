package com.credsentinel.loanrequestmanagement.service;

import com.credsentinel.loanrequestmanagement.dto.request.LoanCreateRequest;
import com.credsentinel.loanrequestmanagement.dto.response.LoanResponse;
import com.credsentinel.loanrequestmanagement.exception.UnsupportedLoanOperationException;
import com.credsentinel.loanrequestmanagement.model.LoanRequestCreatedEvent;
import com.credsentinel.loanrequestmanagement.publisher.LoanRequestEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanCommandService {

    private final LoanService loanService;
    private final LoanRequestEventPublisher loanRequestEventPublisher;

    public LoanResponse createLoan(LoanCreateRequest loanCreateRequest) throws UnsupportedLoanOperationException {

        LoanResponse loan = loanService.createLoan(loanCreateRequest);
        LoanRequestCreatedEvent loanRequestCreatedEvent = LoanRequestCreatedEvent.builder()
                .loanRequestId(loan.getLoanRequestId().toString())
                .createdAt(loan.getCreatedAt())
                .amount(loan.getLoanAmount())
                .tenureDays(loan.getTenureDays())
                .build();

        loanRequestEventPublisher.publish(loanRequestCreatedEvent);

        return loan;
    }

}
