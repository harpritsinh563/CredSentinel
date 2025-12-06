CREATE TABLE repayment_transaction (
    transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_id UUID REFERENCES repayment_schedule(schedule_id),
    loan_request_id UUID REFERENCES loan_request(loan_request_id),
    user_id UUID REFERENCES users(user_id),

    amount_paid NUMERIC(12,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    
    payment_mode VARCHAR(50),   -- UPI / Card / NetBanking
    payment_reference VARCHAR(255),

    created_at TIMESTAMP DEFAULT now()
);
