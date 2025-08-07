-- Add missing is_deleted column to customer_order table
-- This column is expected by the statistics queries

USE vapemall;

-- Add is_deleted column to customer_order table
ALTER TABLE customer_order 
ADD COLUMN is_deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft delete flag: 0=active, 1=deleted';

-- Update existing records to have is_deleted = 0 (active)
UPDATE customer_order SET is_deleted = 0 WHERE is_deleted IS NULL;

-- Verify the column was added
DESCRIBE customer_order;

-- Show current data
SELECT COUNT(*) as total_orders, 
       COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as active_orders,
       COUNT(CASE WHEN is_deleted = 1 THEN 1 END) as deleted_orders
FROM customer_order;
