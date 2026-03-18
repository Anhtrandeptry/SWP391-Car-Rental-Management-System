-- Migration: Add province column to cars table
-- This column stores the province/city selected from dropdown in step 2

ALTER TABLE cars ADD COLUMN province VARCHAR(100) AFTER address;

-- Optional: Update existing records to use province from city field if needed
-- UPDATE cars SET province = city WHERE province IS NULL;
