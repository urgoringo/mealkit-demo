-- Migrate ingredients table ID from BIGSERIAL to UUID
-- WARNING: This migration will clear all data as BIGSERIAL cannot be converted to UUID
TRUNCATE ingredients CASCADE;

-- Drop the default (BIGSERIAL auto-increment)
ALTER TABLE ingredients ALTER COLUMN id DROP DEFAULT;

-- Drop the BIGSERIAL sequence
DROP SEQUENCE IF EXISTS ingredients_id_seq;

-- Change column type to UUID
ALTER TABLE ingredients ALTER COLUMN id SET DATA TYPE UUID;

-- Set new default to generate UUID v7 (will be handled by application)
-- No default needed as application generates UUIDs
