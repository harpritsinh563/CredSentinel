package com.credsentinel.loanrequestmanagement.strategy;

import com.credsentinel.loanrequestmanagement.constants.LoanOperation;

public interface LoanOperationStrategy {

    boolean supports(LoanOperation loanOperation);
    Object execute(Object request);

}
