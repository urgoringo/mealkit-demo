-- Create recipes table
CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    prep_time_minutes INTEGER,
    cook_time_minutes INTEGER,
    servings INTEGER,
    difficulty VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on recipe name for faster searches
CREATE INDEX idx_recipes_name ON recipes(name);

-- Add a comment to the table
COMMENT ON TABLE recipes IS 'Stores recipe information for meal kits';
