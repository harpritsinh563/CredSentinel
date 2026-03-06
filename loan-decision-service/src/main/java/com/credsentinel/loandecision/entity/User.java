package com.credsentinel.loandecision.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private UUID userId;

    @Column(name = "kyc_status")
    private String kycStatus; // 'PENDING', 'VERIFIED', 'REJECTED'

    @Column(name = "primary_identifier")
    private String primaryIdentifier;
}