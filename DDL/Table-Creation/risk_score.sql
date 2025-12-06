CREATE TABLE risk_score (
    risk_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loan_request_id UUID UNIQUE REFERENCES loan_request(loan_request_id),
    
    credit_score INT,
    anomaly_score INT,
    raw_score INT,
    final_risk_score INT NOT NULL,
    
    fraud_flags JSONB,   -- e.g. {"multiple_devices": true, "velocity": "high"}
    
    created_at TIMESTAMP DEFAULT now()
);