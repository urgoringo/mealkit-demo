-- Add status column to orders table
ALTER TABLE orders ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- Create index for filtering by status
CREATE INDEX idx_orders_status ON orders(status);
