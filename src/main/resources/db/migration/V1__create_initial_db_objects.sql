CREATE TABLE am_accounts (
    id NUMBER PRIMARY KEY AUTO_INCREMENT,
    client_id NUMBER NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(19,2) CHECK (balance >= 0) NOT NULL
);

CREATE TABLE am_transactions (
    id NUMBER PRIMARY KEY AUTO_INCREMENT,
    account_id NUMBER NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    description VARCHAR(4000),
    FOREIGN KEY (account_id) REFERENCES am_accounts(id)
);

CREATE TABLE am_clients (
    id NUMBER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE am_currency_rates (
    id NUMBER PRIMARY KEY AUTO_INCREMENT,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    rate NUMBER NOT NULL
);
