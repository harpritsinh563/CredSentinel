package com.credsentinel.loandecision.decision;

import com.credsentinel.loandecision.model.LoanRequestCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoanDecisionEngine {

    public void process(LoanRequestCreatedEvent event){

    }

}
