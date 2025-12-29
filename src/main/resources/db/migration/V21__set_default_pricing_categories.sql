-- Insert default pricing categories (will fail silently if already exist due to unique constraint)
-- LOW
INSERT INTO recipe_pricing_categories (name, price)
SELECT 'LOW', 5.00
WHERE NOT EXISTS (SELECT 1 FROM recipe_pricing_categories WHERE name = 'LOW');

-- MEDIUM
INSERT INTO recipe_pricing_categories (name, price)
SELECT 'MEDIUM', 10.50
WHERE NOT EXISTS (SELECT 1 FROM recipe_pricing_categories WHERE name = 'MEDIUM');

-- HIGH
INSERT INTO recipe_pricing_categories (name, price)
SELECT 'HIGH', 15.00
WHERE NOT EXISTS (SELECT 1 FROM recipe_pricing_categories WHERE name = 'HIGH');

-- Set all recipes without a pricing category to MEDIUM
UPDATE recipes
SET pricing_category_id = (SELECT id FROM recipe_pricing_categories WHERE name = 'MEDIUM')
WHERE pricing_category_id IS NULL;
