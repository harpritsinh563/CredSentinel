package com.credsentinel.loandecision.repository;

import com.credsentinel.loandecision.entity.RiskScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiskScoreRepository extends JpaRepository<RiskScore, UUID> {
    Optional<RiskScore>findByLoanRequestId(UUID loanRequestId);
}
