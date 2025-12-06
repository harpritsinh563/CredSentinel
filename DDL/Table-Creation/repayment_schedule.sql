CREATE TABLE repayment_schedule (
    schedule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loan_request_id UUID REFERENCES loan_request(loan_request_id),
    user_id UUID REFERENCES users(user_id),

    instalment_number INT NOT NULL,       -- 1,2,3,...n
    due_date DATE NOT NULL,
    
    principal_component NUMERIC(12,2),
    interest_component NUMERIC(12,2),
    total_due NUMERIC(12,2) NOT NULL,     -- EMI amount

    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING/PAID/PARTIAL/OVERDUE
    paid_amount NUMERIC(12,2) DEFAULT 0,
    paid_date DATE,

    days_late INT DEFAULT 0,
    penalty_applied NUMERIC(12,2) DEFAULT 0,

    created_at TIMESTAMP DEFAULT now()
);
