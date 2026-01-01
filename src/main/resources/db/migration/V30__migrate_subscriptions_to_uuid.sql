-- Migrate subscriptions table IDs from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
TRUNCATE subscriptions CASCADE;

-- Drop foreign key temporarily
ALTER TABLE subscriptions DROP CONSTRAINT subscriptions_customer_id_fkey;

-- Migrate ID column
ALTER TABLE subscriptions ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS subscriptions_id_seq;
ALTER TABLE subscriptions ALTER COLUMN id SET DATA TYPE UUID;

-- Migrate customer_id foreign key column
ALTER TABLE subscriptions ALTER COLUMN customer_id SET DATA TYPE UUID;

-- Restore foreign key
ALTER TABLE subscriptions ADD CONSTRAINT subscriptions_customer_id_fkey 
    FOREIGN KEY (customer_id) REFERENCES customers(id);
