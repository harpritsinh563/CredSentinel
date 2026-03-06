package com.credsentinel.loanrequestmanagement.repository;

import com.credsentinel.loanrequestmanagement.entities.LoanStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanStatusHistoryRepository extends JpaRepository<LoanStatusHistory, UUID> {
    List<LoanStatusHistory> findByLoanRequestId(UUID loanRequestId);
}
