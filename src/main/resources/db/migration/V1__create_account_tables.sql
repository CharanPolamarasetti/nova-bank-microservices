CREATE TABLE accounts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_accounts_account_number UNIQUE (account_number)
);

CREATE INDEX idx_accounts_customer_id ON accounts (customer_id);

CREATE TABLE beneficiaries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_account_id BIGINT NOT NULL,
    beneficiary_account_number VARCHAR(20) NOT NULL,
    beneficiary_name VARCHAR(150) NOT NULL,
    nickname VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_beneficiaries_owner_account FOREIGN KEY (owner_account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_beneficiaries_owner_account_id ON beneficiaries (owner_account_id);

CREATE TABLE transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference_id VARCHAR(36) NOT NULL,
    from_account_id BIGINT,
    to_account_id BIGINT,
    amount NUMERIC(15,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_transactions_reference_id UNIQUE (reference_id),
    CONSTRAINT fk_transactions_from_account FOREIGN KEY (from_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transactions_to_account FOREIGN KEY (to_account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_transactions_from_account_id ON transactions (from_account_id);
CREATE INDEX idx_transactions_to_account_id ON transactions (to_account_id);
