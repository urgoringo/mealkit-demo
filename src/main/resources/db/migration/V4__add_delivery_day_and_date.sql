-- Add delivery_day column to subscriptions table
ALTER TABLE subscriptions
    ADD COLUMN delivery_day VARCHAR(10);

-- Add delivery_date column to orders table
ALTER TABLE orders
    ADD COLUMN delivery_date DATE;
