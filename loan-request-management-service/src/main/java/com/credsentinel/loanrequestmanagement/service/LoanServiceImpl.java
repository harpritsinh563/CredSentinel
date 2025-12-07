package com.credsentinel.loanrequestmanagement.service;

import com.credsentinel.loanrequestmanagement.dto.request.LoanCreateRequest;
import com.credsentinel.loanrequestmanagement.constants.LoanOperation;
import com.credsentinel.loanrequestmanagement.dto.response.LoanResponse;
import com.credsentinel.loanrequestmanagement.exception.UnsupportedLoanOperationException;
import org.springframework.stereotype.Service;
import com.credsentinel.loanrequestmanagement.strategy.LoanStrategyFactory;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanStrategyFactory loanStrategyFactory;

    public LoanServiceImpl(LoanStrategyFactory loanStrategyFactory){
        this.loanStrategyFactory = loanStrategyFactory;
    }

    @Override
    public LoanResponse createLoan(LoanCreateRequest request) throws UnsupportedLoanOperationException {
        return (LoanResponse) loanStrategyFactory
                .getLoanStrategy(LoanOperation.CREATE)
                .execute(request);
    }
}
