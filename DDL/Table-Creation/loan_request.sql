CREATE TABLE loan_request (
    loan_request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(user_id),
    loan_amount NUMERIC(12,2) NOT NULL,
    tenure_days INT NOT NULL,
    source_channel VARCHAR(50),
    request_payload JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    expires_at TIMESTAMP,
    
    -- status stored temporarily until risk_score is evaluated
    current_status loan_status DEFAULT 'REQUESTED'
);
