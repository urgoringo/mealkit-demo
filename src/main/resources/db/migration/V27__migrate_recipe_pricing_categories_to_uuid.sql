-- Migrate recipe_pricing_categories table ID from BIGSERIAL to UUID
-- WARNING: This migration will clear all data
TRUNCATE recipe_pricing_categories CASCADE;

ALTER TABLE recipe_pricing_categories ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS recipe_pricing_categories_id_seq;
ALTER TABLE recipe_pricing_categories ALTER COLUMN id SET DATA TYPE UUID;
