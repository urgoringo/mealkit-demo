-- Add recipe_ids array column to orders table
ALTER TABLE orders ADD COLUMN recipe_ids BIGINT[] NOT NULL DEFAULT '{}';

-- Migrate existing data from order_recipes junction table to array column
UPDATE orders
SET recipe_ids = (
    SELECT ARRAY_AGG(recipe_id ORDER BY recipe_id)
    FROM order_recipes
    WHERE order_recipes.order_id = orders.id
)
WHERE EXISTS (
    SELECT 1 FROM order_recipes WHERE order_recipes.order_id = orders.id
);
