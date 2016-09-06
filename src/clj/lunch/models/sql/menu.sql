-- name: create-table
-- Create the place table
CREATE TABLE menu (
  id VARCHAR(255) PRIMARY KEY,
  filepath VARCHAR(255) NOT NULL
);

-- name: insert!
-- Insert filepath to a menu by id 
INSERT INTO menu (id, filepath) VALUES (:id, :filepath);

-- name: find-by-id
-- Selects the filepath to a menu by id
SELECT filepath FROM menu WHERE id = :id;
