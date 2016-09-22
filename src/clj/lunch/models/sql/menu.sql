-- name: create-table!
-- Create the place table
CREATE TABLE IF NOT EXISTS menu (
  id VARCHAR(255) PRIMARY KEY,
  link VARCHAR(255) NOT NULL
);

-- name: insert!
-- Insert filepath to a menu by id 
INSERT INTO menu (id, link) VALUES (:id, :link);

-- name: find-by-id
-- Selects the filepath to a menu by id
SELECT link FROM menu WHERE id = :id;

-- name: remove-by-id!
-- Selects the filepath to a menu by id
DELETE FROM menu WHERE id = :id;
