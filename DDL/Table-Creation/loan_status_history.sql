CREATE TABLE loan_status_history (
    status_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loan_request_id UUID REFERENCES loan_request(loan_request_id),
    
    status loan_status NOT NULL,
    reason TEXT,
    source_service VARCHAR(100),   -- loan-service, risk-service, decision-service
    
    timestamp TIMESTAMP DEFAULT now()
);
