package strategy.impl;

import constants.LoanOperation;
import dto.request.LoanCreateRequest;
import dto.response.LoanResponse;
import org.springframework.stereotype.Service;
import strategy.LoanOperationStrategy;

@Service
public class CreateLoanOperationStrategy implements LoanOperationStrategy {
    @Override
    public boolean supports(LoanOperation loanOperation) {
        return loanOperation.equals(LoanOperation.CREATE);
    }

    @Override
    public Object execute(Object request) {
        LoanCreateRequest req = (LoanCreateRequest) request;
        // validate
        // save loan_request
        // publish Kafka event
        return new LoanResponse();
    }
}
