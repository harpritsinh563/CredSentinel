package com.credsentinel.loandecision.consumer;

import com.credsentinel.loandecision.decision.LoanDecisionEngine;
import com.credsentinel.loandecision.model.LoanRequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoanRequestEventListener {

    private final LoanDecisionEngine loanDecisionEngine;

    @KafkaListener(
            topics = "CredSentinel.LoanRequestCreated",
            groupId = "CredSentinel.LoanDecisionService"
    )
    public void onMessage(@Payload LoanRequestCreatedEvent event, Acknowledgment acknowledgment){
        try{
            loanDecisionEngine.process(event);
            acknowledgment.acknowledge(); // Commit offset
        }
        catch (Exception e){
            log.error("Exception occurred while processing the loan request : ",e);
        }
    }

}
