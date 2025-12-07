package com.credsentinel.loanrequestmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.credsentinel.loanrequestmanagement.repository")
@EntityScan(basePackages = "com.credsentinel.loanrequestmanagement.entities")
@ComponentScan("com.credsentinel.loanrequestmanagement.*")
public class LoanRequestManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanRequestManagementApplication.class,args);
    }

}
