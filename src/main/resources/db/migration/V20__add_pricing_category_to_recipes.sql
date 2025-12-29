ALTER TABLE recipes ADD COLUMN pricing_category_id BIGINT;

ALTER TABLE recipes ADD CONSTRAINT fk_recipes_pricing_category
    FOREIGN KEY (pricing_category_id) REFERENCES recipe_pricing_categories(id);

CREATE INDEX idx_recipes_pricing_category_id ON recipes(pricing_category_id);
