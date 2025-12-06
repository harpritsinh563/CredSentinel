CREATE TABLE device_info (
    device_id VARCHAR(255) PRIMARY KEY,  -- device fingerprint hash
    user_id UUID REFERENCES users(user_id),
    ip_address INET,
    location_city VARCHAR(100),
    created_at TIMESTAMP DEFAULT now()
);
