-- Migrate subscriptions table IDs from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
-- Note: customer_id FK was already dropped in V29

-- Drop dependent foreign keys first (will be recreated in V31)
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_subscription_id_fkey;

-- Clear data
TRUNCATE subscriptions CASCADE;

-- Migrate ID column
ALTER TABLE subscriptions ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS subscriptions_id_seq;
ALTER TABLE subscriptions ALTER COLUMN id SET DATA TYPE UUID USING gen_random_uuid();

-- Migrate customer_id foreign key column
ALTER TABLE subscriptions ALTER COLUMN customer_id SET DATA TYPE UUID USING gen_random_uuid();

-- Restore customer_id foreign key
ALTER TABLE subscriptions ADD CONSTRAINT subscriptions_customer_id_fkey 
    FOREIGN KEY (customer_id) REFERENCES customers(id);
