-- Update ingredients_with_details to store ingredient_id instead of name
-- The JSONB structure changes from:
-- [{"name": "chicken", "quantity": "500", "unit": "g"}]
-- to:
-- [{"ingredient_id": 1, "quantity": "500", "unit": "g"}]

-- Note: This migration doesn't migrate existing data as we're in development.
-- In production, you would need a data migration script.
COMMENT ON COLUMN recipes.ingredients_with_details IS 'Recipe ingredients with ingredient_id, quantity, and unit';
