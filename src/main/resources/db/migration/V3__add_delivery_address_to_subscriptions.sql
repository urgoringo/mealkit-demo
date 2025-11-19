-- Add delivery address column to subscriptions table
ALTER TABLE subscriptions
ADD COLUMN delivery_address TEXT;
