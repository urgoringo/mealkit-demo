-- Migrate orders table IDs from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
-- Note: subscription_id FK was already dropped in V30

-- Clear data
TRUNCATE orders CASCADE;

-- Migrate ID column
ALTER TABLE orders ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS orders_id_seq;
ALTER TABLE orders ALTER COLUMN id SET DATA TYPE UUID USING gen_random_uuid();

-- Migrate subscription_id foreign key column
ALTER TABLE orders ALTER COLUMN subscription_id SET DATA TYPE UUID USING gen_random_uuid();

-- Migrate recipe_ids array column (drop default if any)
ALTER TABLE orders ALTER COLUMN recipe_ids DROP DEFAULT;
ALTER TABLE orders ALTER COLUMN recipe_ids SET DATA TYPE UUID[] USING ARRAY[]::UUID[];

-- Restore subscription_id foreign key
ALTER TABLE orders ADD CONSTRAINT orders_subscription_id_fkey 
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id);
