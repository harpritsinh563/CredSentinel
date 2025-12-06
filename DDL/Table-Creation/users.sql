CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    primary_identifier VARCHAR(255) UNIQUE NOT NULL,
    login_provider login_provider NOT NULL,
    kyc_status kyc_status DEFAULT 'PENDING',
    pan_number VARCHAR(20),
    aadhaar_last4 CHAR(4),
    last_login_ip INET,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);
