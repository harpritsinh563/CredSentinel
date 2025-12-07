package service;

import dto.request.LoanCreateRequest;
import dto.response.LoanResponse;
import exception.UnsupportedLoanOperationException;

public interface LoanService {
    public LoanResponse createLoan(LoanCreateRequest request) throws UnsupportedLoanOperationException;
}
