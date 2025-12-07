package strategy;

import constants.LoanOperation;
import exception.UnsupportedLoanOperationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoanStrategyFactory {

    private final List<LoanOperationStrategy> loanOperationStrategies;

    public LoanStrategyFactory(List<LoanOperationStrategy> loanOperationStrategies){
        this.loanOperationStrategies = loanOperationStrategies;
    }

    public LoanOperationStrategy getLoanStrategy(LoanOperation loanOperation) throws UnsupportedLoanOperationException {
        return loanOperationStrategies.stream().filter(loanOperationStrategy -> loanOperationStrategy.supports(loanOperation)).findFirst().orElseThrow(() -> new UnsupportedLoanOperationException("Unsupported Loan Operation : "+loanOperation.name()));
    }

}
