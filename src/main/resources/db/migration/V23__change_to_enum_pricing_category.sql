-- Add pricing_category column to store enum as string
ALTER TABLE recipes ADD COLUMN pricing_category VARCHAR(50);

-- Migrate data from pricing_category_id to pricing_category
UPDATE recipes 
SET pricing_category = (
    SELECT name FROM recipe_pricing_categories 
    WHERE id = recipes.pricing_category_id
);

-- Make pricing_category NOT NULL
ALTER TABLE recipes ALTER COLUMN pricing_category SET NOT NULL;

-- Drop the old foreign key and pricing_category_id column
ALTER TABLE recipes DROP CONSTRAINT IF EXISTS fk_recipes_pricing_category;
ALTER TABLE recipes DROP COLUMN pricing_category_id;

-- Drop the recipe_pricing_categories table (no longer needed)
DROP TABLE IF EXISTS recipe_pricing_categories;
