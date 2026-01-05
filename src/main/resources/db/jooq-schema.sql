-- Final schema state for jOOQ code generation
-- This represents the schema after all Flyway migrations have been applied

-- Recipes table
CREATE TABLE recipes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    instructions TEXT[],
    ingredients JSONB,
    pricing_category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recipes_name ON recipes(name);
CREATE INDEX idx_recipes_ingredients ON recipes USING GIN (ingredients);
CREATE INDEX idx_recipes_pricing_category ON recipes(pricing_category);
COMMENT ON TABLE recipes IS 'Stores recipe information for meal kits';

-- Customers table
CREATE TABLE customers (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Subscriptions table
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    delivery_day VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Orders table
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    subscription_id UUID,
    delivery_date DATE,
    status VARCHAR(20) NOT NULL,
    recipe_ids UUID[] NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);

-- Ingredients table
CREATE TABLE ingredients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ingredients_name ON ingredients(name);
COMMENT ON TABLE ingredients IS 'Stores ingredient master data';

-- Backoffice users table
CREATE TABLE backoffice_users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Recipe pricing categories table
CREATE TABLE recipe_pricing_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recipe_pricing_categories_name ON recipe_pricing_categories(name);
COMMENT ON TABLE recipe_pricing_categories IS 'Stores pricing categories for recipes (LOW, MEDIUM, HIGH)';

-- Add foreign key to recipes
ALTER TABLE recipes ADD CONSTRAINT fk_recipes_pricing_category_name
    FOREIGN KEY (pricing_category) REFERENCES recipe_pricing_categories(name);

-- Scheduled tasks table (db-scheduler)
CREATE TABLE scheduled_tasks (
    task_name VARCHAR(255) NOT NULL,
    task_instance VARCHAR(255) NOT NULL,
    task_data BYTEA,
    execution_time timestamptz NOT NULL,
    picked BOOLEAN NOT NULL,
    picked_by VARCHAR(50),
    last_success timestamptz,
    last_failure timestamptz,
    consecutive_failures INT,
    last_heartbeat timestamptz,
    version BIGINT NOT NULL,
    PRIMARY KEY (task_name, task_instance)
);

CREATE INDEX idx_scheduled_tasks_execution_time ON scheduled_tasks(execution_time);
CREATE INDEX idx_scheduled_tasks_last_heartbeat ON scheduled_tasks(last_heartbeat);
