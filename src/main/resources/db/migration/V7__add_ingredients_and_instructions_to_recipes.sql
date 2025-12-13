-- Add ingredients and instructions columns to recipes table
ALTER TABLE recipes ADD COLUMN ingredients TEXT[] DEFAULT '{}';
ALTER TABLE recipes ADD COLUMN instructions TEXT[] DEFAULT '{}';
