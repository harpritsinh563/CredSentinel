package com.credsentinel.loanrequestmanagement.publisher;

import com.credsentinel.loanrequestmanagement.model.LoanRequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class LoanRequestEventPublisher {

    private final KafkaTemplate<String, LoanRequestCreatedEvent> kafkaTemplate;

    @Value("${kafka.topics.loan-request-created}")
    private String topic;

    public void publish(LoanRequestCreatedEvent loanRequestCreatedEvent){
        try {
            kafkaTemplate.send(topic,loanRequestCreatedEvent.getLoanRequestId(),loanRequestCreatedEvent);
            log.info("Message produced successfully on the topic : {} with id : {}",topic,loanRequestCreatedEvent.getLoanRequestId());
        }catch (Exception e){
            log.error("Exception occurred in publishing event on topic : {}",topic,e);
        }
    }


}
