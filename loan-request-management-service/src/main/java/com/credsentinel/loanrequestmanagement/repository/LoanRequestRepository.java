package com.credsentinel.loanrequestmanagement.repository;

import com.credsentinel.loanrequestmanagement.entities.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoanRequestRepository extends JpaRepository<LoanRequest, UUID> {
}
