package com.credsentinel.loandecision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class LoanDecisionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanDecisionServiceApplication.class,args);
    }
}
