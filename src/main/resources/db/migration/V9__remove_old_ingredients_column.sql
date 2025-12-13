-- Remove old ingredients column now that we use ingredients_with_details
ALTER TABLE recipes DROP COLUMN ingredients;
