CREATE TABLE recipe_pricing_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recipe_pricing_categories_name ON recipe_pricing_categories(name);

COMMENT ON TABLE recipe_pricing_categories IS 'Stores pricing categories for recipes (LOW, MEDIUM, HIGH)';
