-- data.sql - Seed initial Manager user
-- IMPORTANT: Replace '$2a$10$...' with a BCRYPT HASH of your desired manager password (e.g., "managerpass")
-- You can generate one using an online tool or a simple Java program with BCryptPasswordEncoder

-- Delete existing manager user/profile first if re-running script to avoid unique constraint errors
DELETE FROM manager WHERE user_id IN (SELECT id FROM users WHERE username = 'manager');
DELETE FROM users WHERE username = 'manager';

-- Insert the User record for the manager
INSERT INTO users (username, password, role, enabled) VALUES
    ('manager', '$2a$12$DnI9vzUkevJ3PMdePXlAn.Q9c/qAjcsOgRRewJMjrsRsUVWW9WM9W', 'MANAGER', true);
-- The hash above is for 'managerpass' - REPLACE WITH YOUR OWN HASH

-- Insert the Manager profile, linking it to the User record
-- Assumes user ID is auto-incremented and the previous insert resulted in ID 1.
-- If using a different DB or strategy, adjust how you get the user_id.
-- Using a subquery is safer if ID is not guaranteed.
INSERT INTO manager (name, employee_id, user_id) VALUES
    ('Default Manager', 'MGR001', (SELECT id FROM users WHERE username = 'manager'));

-- You could potentially add initial Staff or Customers here for testing,
-- but the requirement was only for the Manager seed.
-- Example (Staff):
-- INSERT INTO users (username, password, role, enabled) VALUES ('staff1', '$2a$10$...', 'STAFF', true);
-- INSERT INTO staff (name, employee_id, user_id) VALUES ('Test Staff', 'STF001', (SELECT id FROM users WHERE username = 'staff1'));