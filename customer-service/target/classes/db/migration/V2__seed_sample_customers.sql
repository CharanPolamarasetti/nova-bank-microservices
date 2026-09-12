-- Sample/seed data for local development and manual testing only.
-- All seeded customers share the password: Password123!
-- Hash below is a genuine bcrypt hash (cost factor 10, $2b$ format), generated
-- offline - NOT copied from any real system. Do not reuse these values,
-- or this migration, in a real environment.

INSERT INTO customers (id, full_name, email, password_hash, phone_number, status, created_at, updated_at)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Asha Rao',    'asha.rao@example.com',    '$2b$10$KRUuUWoR/wmpktMv6HvWAeiH6Kikp61wpD0TXV/4y/DhMiGn2YpWW', '+91-9000000001', 'ACTIVE', now(), now()),
    (2, 'Vikram Shah', 'vikram.shah@example.com', '$2b$10$KRUuUWoR/wmpktMv6HvWAeiH6Kikp61wpD0TXV/4y/DhMiGn2YpWW', '+91-9000000002', 'ACTIVE', now(), now()),
    (3, 'Meera Iyer',  'meera.iyer@example.com',  '$2b$10$KRUuUWoR/wmpktMv6HvWAeiH6Kikp61wpD0TXV/4y/DhMiGn2YpWW', '+91-9000000003', 'ACTIVE', now(), now());

-- Keep the identity sequence ahead of the explicit ids inserted above, so the
-- next application-created customer doesn't collide with id 1/2/3.
SELECT setval(pg_get_serial_sequence('customers', 'id'), (SELECT MAX(id) FROM customers));
