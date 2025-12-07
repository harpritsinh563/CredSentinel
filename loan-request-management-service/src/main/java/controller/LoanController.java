package controller;

import constants.EndpointConstants;
import dto.request.LoanCreateRequest;
import dto.response.LoanResponse;
import exception.UnsupportedLoanOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.LoanService;

@RestController
@RequestMapping(EndpointConstants.BASE)
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@RequestBody LoanCreateRequest loanCreateRequest) throws UnsupportedLoanOperationException {
        LoanResponse loanResponse = loanService.createLoan(loanCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);
    }

}
