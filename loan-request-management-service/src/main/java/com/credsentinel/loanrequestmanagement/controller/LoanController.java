package com.credsentinel.loanrequestmanagement.controller;

import com.credsentinel.loanrequestmanagement.constants.EndpointConstants;
import com.credsentinel.loanrequestmanagement.dto.request.LoanCreateRequest;
import com.credsentinel.loanrequestmanagement.dto.response.LoanResponse;
import com.credsentinel.loanrequestmanagement.exception.UnsupportedLoanOperationException;
import com.credsentinel.loanrequestmanagement.service.LoanCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.credsentinel.loanrequestmanagement.service.LoanService;

@RestController
@RequestMapping(EndpointConstants.BASE)
@RequiredArgsConstructor
public class LoanController {

    private final LoanCommandService loanCommandService;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanCreateRequest loanCreateRequest) throws UnsupportedLoanOperationException {
        LoanResponse loanResponse = loanCommandService.createLoan(loanCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);
    }

}
