-- Make pricing_category_id required
ALTER TABLE recipes ALTER COLUMN pricing_category_id SET NOT NULL;
