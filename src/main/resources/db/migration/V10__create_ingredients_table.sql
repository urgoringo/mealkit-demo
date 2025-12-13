-- Create ingredients table to store ingredient entities
CREATE TABLE ingredients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on ingredient name for faster searches
CREATE INDEX idx_ingredients_name ON ingredients(name);

-- Add comment to the table
COMMENT ON TABLE ingredients IS 'Stores ingredient master data';
