-- Migrate customers table ID from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
TRUNCATE customers CASCADE;

ALTER TABLE customers ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS customers_id_seq;
ALTER TABLE customers ALTER COLUMN id SET DATA TYPE UUID;
