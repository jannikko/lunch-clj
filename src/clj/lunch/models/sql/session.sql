-- name: create-session-id!
-- Create the session_id table
CREATE TABLE IF NOT EXISTS session_id (
  id UUID PRIMARY KEY,
  created timestamp default current_timestamp,
  place_id VARCHAR(255) NOT NULL
);

-- name: create-session-entry!
-- Create the session_entry table
CREATE TABLE IF NOT EXISTS session_entry (
  id SERIAL PRIMARY KEY,
  session_id UUID REFERENCES session_id NOT NULL,
  name VARCHAR(255) NOT NULL,
  lunch_order VARCHAR(255) NOT NULL,
  last_update timestamp default current_timestamp
);

-- name: find-session-id
-- Create the session_id table
SELECT * from session_id WHERE id = :id;

-- name: insert-session-id!
-- Create the session_id table
INSERT INTO session_id (id, place_id) VALUES (:id, :place_id);

-- name: update-session-entry!
-- Create the session_id table
UPDATE session_entry SET name = :name, lunch_order = :lunch_order WHERE id = :id;

-- name: insert-session-entry<!
-- Create the session_id table
INSERT INTO session_entry (session_id, name, lunch_order) VALUES (:session_id, :name, :lunch_order);


-- name: find-session-entries
-- Create the session_id table
SELECT * FROM session_entry WHERE session_id = :session_id;

