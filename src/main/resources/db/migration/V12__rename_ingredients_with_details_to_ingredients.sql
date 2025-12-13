-- Rename ingredients_with_details column to ingredients
ALTER TABLE recipes RENAME COLUMN ingredients_with_details TO ingredients;

-- Drop old index on ingredients_with_details
DROP INDEX IF EXISTS idx_recipes_ingredients_with_details;

-- Create new index on ingredients column
CREATE INDEX idx_recipes_ingredients ON recipes USING GIN (ingredients);

-- Update comment
COMMENT ON COLUMN recipes.ingredients IS 'Recipe ingredients with ingredient_id, quantity, and unit';
