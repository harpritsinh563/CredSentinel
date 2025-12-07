package com.credsentinel.loanrequestmanagement.service;

import com.credsentinel.loanrequestmanagement.dto.request.LoanCreateRequest;
import com.credsentinel.loanrequestmanagement.dto.response.LoanResponse;
import com.credsentinel.loanrequestmanagement.exception.UnsupportedLoanOperationException;

public interface LoanService {
    public LoanResponse createLoan(LoanCreateRequest request) throws UnsupportedLoanOperationException;
}
