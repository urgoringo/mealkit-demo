-- Add ingredients_with_details column to store structured ingredient data
ALTER TABLE recipes ADD COLUMN ingredients_with_details JSONB DEFAULT '[]'::jsonb;

-- Create index on ingredients_with_details for faster queries
CREATE INDEX idx_recipes_ingredients_with_details ON recipes USING GIN (ingredients_with_details);

-- Add comment to the column
COMMENT ON COLUMN recipes.ingredients_with_details IS 'Structured ingredient data with name, quantity, and unit';
