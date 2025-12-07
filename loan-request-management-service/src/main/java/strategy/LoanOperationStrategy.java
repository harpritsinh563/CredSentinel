package strategy;

import constants.LoanOperation;

public interface LoanOperationStrategy {

    boolean supports(LoanOperation loanOperation);
    Object execute(Object request);

}
