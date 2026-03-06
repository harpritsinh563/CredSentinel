package com.credsentinel.loandecision.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "risk_score")
@Data
public class RiskScore {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID riskId;

    @Column(name = "loan_request_id", unique = true)
    private UUID loanRequestId;

    private Integer creditScore;
    private Integer anomalyScore;
    private Integer finalRiskScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "fraud_flags")
    private String fraudFlags; // Stores reasons like "velocity_high"
}
