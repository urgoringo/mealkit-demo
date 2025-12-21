-- Rename password_hash to password in backoffice_users table for consistency with customers table
ALTER TABLE backoffice_users RENAME COLUMN password_hash TO password;
