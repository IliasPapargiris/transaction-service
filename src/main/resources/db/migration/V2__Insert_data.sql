-- Currencies
INSERT INTO currencies (code, name) VALUES
  ('BGN', 'Bulgarian Lev'),
  ('EUR', 'Euro'),
  ('GBP', 'British Pound'),
  ('USD', 'US Dollar');

-- Countries
INSERT INTO countries (code, name) VALUES
  ('BG', 'Bulgaria'),
  ('GB', 'United Kingdom'),
  ('GR', 'Greece'),
  ('US', 'United States');

-- Country Supported Currencies
INSERT INTO country_supported_currencies (id, country_code, currency_code) VALUES
  (1, 'BG', 'BGN'),
  (2, 'BG', 'EUR'),
  (3, 'GB', 'GBP'),
  (4, 'GR', 'EUR'),
  (5, 'US', 'USD');

-- Users
INSERT INTO users (name, email, created_at, password, role)
VALUES (
    'admin',
    'admin@admin.com',
    TIMESTAMP '2025-05-05 14:08:27',
    '$2a$10$bjzToWFx4rQUhFIqJK7TPebKl8VMDssXM.0qGRGJfqpfjf.ONiUvG',
    'ADMIN'
);



-- Transactions
INSERT INTO transactions (user_id, country_code, amount, currency, timestamp)
VALUES
-- Bulgaria: BGN, EUR
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'BG', 120.00, 'BGN', TIMESTAMP '2025-05-01 09:00:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'BG', 180.25, 'EUR', TIMESTAMP '2025-05-01 15:30:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'BG', 50.00, 'BGN', TIMESTAMP '2025-05-01 20:00:00'),

-- Greece: EUR
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'GR', 150.00, 'EUR', TIMESTAMP '2025-05-02 10:15:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'GR', 300.00, 'EUR', TIMESTAMP '2025-05-02 14:45:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'GR', 70.00, 'EUR', TIMESTAMP '2025-05-02 18:00:00'),

-- UK: GBP
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'GB', 100.00, 'GBP', TIMESTAMP '2025-05-03 12:00:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'GB', 200.00, 'GBP', TIMESTAMP '2025-05-03 16:00:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'GB', 50.00, 'GBP', TIMESTAMP '2025-05-03 20:00:00'),

-- US: USD
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'US', 200.50, 'USD', TIMESTAMP '2025-05-04 12:30:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'US', 80.00, 'USD', TIMESTAMP '2025-05-04 14:00:00'),
((SELECT id FROM users WHERE email = 'admin@admin.com'), 'US', 400.00, 'USD', TIMESTAMP '2025-05-04 17:15:00');
