-- Login providers
CREATE TYPE login_provider AS ENUM ('EMAIL', 'GOOGLE', 'FACEBOOK', 'OTP');

-- KYC status
CREATE TYPE kyc_status AS ENUM ('PENDING', 'VERIFIED', 'REJECTED');

-- Loan status states
CREATE TYPE loan_status AS ENUM (
    'REQUESTED',
    'RISK_PENDING',
    'RISK_EVALUATED',
    'APPROVED',
    'REJECTED',
    'FRAUD_FLAGGED'
);
