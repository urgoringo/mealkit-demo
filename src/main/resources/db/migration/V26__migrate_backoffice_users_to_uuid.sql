-- Migrate backoffice_users table ID from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
TRUNCATE backoffice_users CASCADE;

ALTER TABLE backoffice_users ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS backoffice_users_id_seq;
ALTER TABLE backoffice_users ALTER COLUMN id SET DATA TYPE UUID USING gen_random_uuid();
