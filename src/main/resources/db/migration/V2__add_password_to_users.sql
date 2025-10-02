-- Add password column to users table as nullable first
ALTER TABLE users ADD COLUMN password VARCHAR(255);

-- Update existing users with a default encrypted password
-- This is a BCrypt hash for 'password123' - users should change this
UPDATE users SET password = '$2a$10$N.zmdr9k7uOCQb00gEo/OOKBdaF.JL4AZLa6LmIJRGrjJyJHvFTi2' WHERE password IS NULL;

-- Now make the column NOT NULL
ALTER TABLE users ALTER COLUMN password SET NOT NULL;