
INSERT INTO users (username, password, role, enabled) VALUES
    ('manager', '$2a$12$DnI9vzUkevJ3PMdePXlAn.Q9c/qAjcsOgRRewJMjrsRsUVWW9WM9W', 'MANAGER', true)
ON CONFLICT (username) DO NOTHING; -- If a user with this username already exists, do nothing.
                                   -- Assumes 'username' has a UNIQUE constraint.

INSERT INTO manager (name, employee_id, user_id)
SELECT 'Default Manager', 'MGR001', u.id
FROM users u
WHERE u.username = 'manager'
ON CONFLICT (user_id) DO NOTHING; -- If a manager profile for this user_id already exists, do nothing.
                                 -- Assumes 'user_id' in the 'manager' table has a UNIQUE constraint or is the PK.
