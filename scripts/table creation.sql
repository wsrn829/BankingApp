CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20)
);


CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);


CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    account_id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    balance_after_transaction DECIMAL(15, 2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
