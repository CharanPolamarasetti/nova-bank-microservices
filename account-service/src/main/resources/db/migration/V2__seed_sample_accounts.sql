-- Sample/seed data for local development and manual testing only.
-- customer_id values below correspond to the customers seeded in
-- customer-service's V2 migration (Asha Rao=1, Vikram Shah=2, Meera Iyer=3).
-- This is illustrative data to exercise the API end-to-end - it is NOT a
-- reconciled ledger (the starting balances were chosen for realism, not
-- derived by replaying the sample transactions below).

INSERT INTO accounts (id, account_number, customer_id, account_type, balance, status, created_at, updated_at)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 'NB0000000001', 1, 'SAVINGS', 125000.00, 'ACTIVE', now(), now()),
    (2, 'NB0000000002', 1, 'CURRENT',  45000.00, 'ACTIVE', now(), now()),
    (3, 'NB0000000003', 2, 'SAVINGS',  82000.00, 'ACTIVE', now(), now()),
    (4, 'NB0000000004', 2, 'CURRENT',  15000.00, 'ACTIVE', now(), now()),
    (5, 'NB0000000005', 3, 'SAVINGS', 260000.00, 'ACTIVE', now(), now()),
    (6, 'NB0000000006', 3, 'CURRENT',   5000.00, 'ACTIVE', now(), now());

SELECT setval(pg_get_serial_sequence('accounts', 'id'), (SELECT MAX(id) FROM accounts));

INSERT INTO beneficiaries (id, owner_account_id, beneficiary_account_number, beneficiary_name, nickname, created_at)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 1, 'NB0000000003', 'Vikram Shah', 'Vikram', now()),
    (2, 3, 'NB0000000001', 'Asha Rao',    'Asha',   now());

SELECT setval(pg_get_serial_sequence('beneficiaries', 'id'), (SELECT MAX(id) FROM beneficiaries));

INSERT INTO transactions (id, reference_id, from_account_id, to_account_id, amount, transaction_type, status, description, created_at)
OVERRIDING SYSTEM VALUE
VALUES
    (1, '11111111-1111-1111-1111-111111111111', 1, 3, 5000.00,  'TRANSFER', 'SUCCESS', 'Rent share',       now() - interval '10 days'),
    (2, '22222222-2222-2222-2222-222222222222', 3, 1, 1200.00,  'TRANSFER', 'SUCCESS', 'Lunch split',      now() - interval '7 days'),
    (3, '33333333-3333-3333-3333-333333333333', 5, 1, 20000.00, 'TRANSFER', 'SUCCESS', 'Loan repayment',   now() - interval '5 days'),
    (4, '44444444-4444-4444-4444-444444444444', 2, 4, 3000.00,  'TRANSFER', 'SUCCESS', 'Utility bill',     now() - interval '2 days'),
    (5, '55555555-5555-5555-5555-555555555555', 6, 5, 2000.00,  'TRANSFER', 'SUCCESS', 'Move to savings',  now() - interval '1 days');

SELECT setval(pg_get_serial_sequence('transactions', 'id'), (SELECT MAX(id) FROM transactions));
