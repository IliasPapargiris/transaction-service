-- Countries
CREATE TABLE countries (
  code VARCHAR(2) NOT NULL,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (code)
);

-- Currencies
CREATE TABLE currencies (
  code VARCHAR(3) NOT NULL,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (code)
);

-- Country Supported Currencies
CREATE TABLE country_supported_currencies (
  id BIGSERIAL PRIMARY KEY,
  country_code VARCHAR(2) NOT NULL,
  currency_code VARCHAR(3) NOT NULL,
  UNIQUE (country_code, currency_code),
  FOREIGN KEY (country_code) REFERENCES countries(code),
  FOREIGN KEY (currency_code) REFERENCES currencies(code)
);

-- Users
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'USER'
);

-- Transactions
CREATE TABLE transactions (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  country_code VARCHAR(2) NOT NULL,
  amount NUMERIC(15, 2) NOT NULL CHECK (amount >= 0.01),
  currency VARCHAR(3) NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  UNIQUE (user_id, country_code, amount, currency, timestamp),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (country_code) REFERENCES countries(code)
);
