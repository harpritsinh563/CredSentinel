package service;

import constants.LoanOperation;
import dto.request.LoanCreateRequest;
import dto.response.LoanResponse;
import exception.UnsupportedLoanOperationException;
import org.springframework.stereotype.Service;
import strategy.LoanStrategyFactory;

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
