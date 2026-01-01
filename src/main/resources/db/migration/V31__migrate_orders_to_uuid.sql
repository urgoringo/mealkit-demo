-- Migrate orders table IDs from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
TRUNCATE orders CASCADE;

-- Drop foreign key temporarily
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_subscription_id_fkey;

-- Migrate ID column
ALTER TABLE orders ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS orders_id_seq;
ALTER TABLE orders ALTER COLUMN id SET DATA TYPE UUID;

-- Migrate subscription_id foreign key column
ALTER TABLE orders ALTER COLUMN subscription_id SET DATA TYPE UUID;

-- Migrate recipe_ids array column
ALTER TABLE orders ALTER COLUMN recipe_ids SET DATA TYPE UUID[];

-- Restore foreign key
ALTER TABLE orders ADD CONSTRAINT orders_subscription_id_fkey 
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id);
