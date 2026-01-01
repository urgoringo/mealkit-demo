-- Re-insert pricing categories after UUID migration (they were cleared by TRUNCATE in V27)

INSERT INTO recipe_pricing_categories (id, name, price)
VALUES
    (gen_random_uuid(), 'LOW', 5.00),
    (gen_random_uuid(), 'MEDIUM', 10.50),
    (gen_random_uuid(), 'HIGH', 15.00);
