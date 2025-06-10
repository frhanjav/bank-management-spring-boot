-- data.sql - Seed initial Manager user for PostgreSQL

-- Delete existing manager user/profile first if re-running script to avoid unique constraint errors
-- This part is fine for PostgreSQL as well, assuming tables exist.
-- If using ON CONFLICT for inserts, these DELETEs might become less critical for simple seeding,
-- but are good for ensuring a clean state if you modify the manager's details.
DELETE FROM manager WHERE user_id IN (SELECT id FROM users WHERE username = 'manager');
DELETE FROM users WHERE username = 'manager';

-- Insert the User record for the manager
INSERT INTO users (username, password, role, enabled) VALUES
    ('manager', '$2a$12$DnI9vzUkevJ3PMdePXlAn.Q9c/qAjcsOgRRewJMjrsRsUVWW9WM9W', 'MANAGER', true)
ON CONFLICT (username) DO NOTHING; -- If a user with this username already exists, do nothing.
                                   -- Assumes 'username' has a UNIQUE constraint.

-- Insert the Manager profile, linking it to the User record
-- This uses a subquery which is good practice and works well in PostgreSQL.
INSERT INTO manager (name, employee_id, user_id)
SELECT 'Default Manager', 'MGR001', u.id
FROM users u
WHERE u.username = 'manager'
ON CONFLICT (user_id) DO NOTHING; -- If a manager profile for this user_id already exists, do nothing.
                                 -- Assumes 'user_id' in the 'manager' table has a UNIQUE constraint or is the PK.

-- You could potentially add initial Staff or Customers here for testing,
-- but the requirement was only for the Manager seed.
-- Example (Staff for PostgreSQL):
-- INSERT INTO users (username, password, role, enabled) VALUES ('staff1', '$2a$10$YourStaffHashHere', 'STAFF', true) ON CONFLICT (username) DO NOTHING;
-- INSERT INTO staff (name, employee_id, user_id) SELECT 'Test Staff', 'STF001', u.id FROM users u WHERE u.username = 'staff1' ON CONFLICT (user_id) DO NOTHING;