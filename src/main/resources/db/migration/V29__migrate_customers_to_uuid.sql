-- Migrate customers table ID from BIGSERIAL to UUID
-- WARNING: This migration will clear all data

-- Drop dependent foreign keys first (will be recreated in V30)
ALTER TABLE subscriptions DROP CONSTRAINT IF EXISTS subscriptions_customer_id_fkey;

-- Clear data
TRUNCATE customers CASCADE;

-- Migrate ID column
ALTER TABLE customers ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS customers_id_seq;
ALTER TABLE customers ALTER COLUMN id SET DATA TYPE UUID USING gen_random_uuid();
