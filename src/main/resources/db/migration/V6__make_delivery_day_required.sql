-- Make delivery_day column NOT NULL in subscriptions table
-- First set a default value for any existing NULL values
UPDATE subscriptions
SET delivery_day = 'WEDNESDAY'
WHERE delivery_day IS NULL;

-- Now make the column NOT NULL
ALTER TABLE subscriptions
    ALTER COLUMN delivery_day SET NOT NULL;
