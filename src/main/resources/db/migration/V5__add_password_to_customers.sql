-- Add password column to customers table
ALTER TABLE customers ADD COLUMN password VARCHAR(255) NOT NULL;
