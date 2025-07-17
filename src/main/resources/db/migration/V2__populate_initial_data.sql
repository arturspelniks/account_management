INSERT INTO am_clients (id, name) VALUES
  (1, 'Alice'),
  (2, 'Bob');

INSERT INTO am_accounts (client_id, currency, balance) VALUES
  (1, 'EUR', 1000.00),
  (1, 'USD', 800.00),
  (1, 'CAD', 1200.00),
  (1, 'JPY', 150000.00),
  (2, 'USD', 500.00),
  (2, 'EUR', 500.00);

INSERT INTO am_currency_rates (from_currency, to_currency, rate) VALUES
  ('EUR', 'USD', 1.10),
  ('EUR', 'CAD', 1.45),
  ('EUR', 'JPY', 160.00),
  ('USD', 'EUR', 0.91),
  ('USD', 'CAD', 1.32),
  ('USD', 'JPY', 145.00),
  ('CAD', 'EUR', 0.69),
  ('CAD', 'USD', 0.76),
  ('CAD', 'JPY', 110.00),
  ('JPY', 'EUR', 0.0063),
  ('JPY', 'USD', 0.0069),
  ('JPY', 'CAD', 0.0091);

INSERT INTO am_transactions (account_id, amount, currency, timestamp, description)
SELECT a1.id, -1.00, a1.currency, CURRENT_TIMESTAMP, 'Transfer to account ' || a2.id
FROM am_accounts a1
JOIN am_accounts a2 ON a1.id <> a2.id
WHERE a1.currency = a2.currency;

INSERT INTO am_transactions (account_id, amount, currency, timestamp, description)
SELECT a2.id, 1.00, a1.currency, CURRENT_TIMESTAMP, 'Transfer from account ' || a1.id
FROM am_accounts a1
JOIN am_accounts a2 ON a1.id <> a2.id
WHERE a1.currency = a2.currency;
