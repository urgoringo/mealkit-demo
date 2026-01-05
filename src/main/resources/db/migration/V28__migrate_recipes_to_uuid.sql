-- Migrate recipes table ID from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
TRUNCATE recipes CASCADE;

ALTER TABLE recipes ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS recipes_id_seq;
ALTER TABLE recipes ALTER COLUMN id SET DATA TYPE UUID USING gen_random_uuid();
