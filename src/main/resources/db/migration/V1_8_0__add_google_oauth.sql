ALTER TABLE users
ALTER COLUMN password
DROP NOT NULL;

ALTER TABLE users
ADD COLUMN auth_provider VARCHAR(255) CHECK (auth_provider IN ('CREDENTIALS', 'GOOGLE'));

UPDATE users
SET
  auth_provider = COALESCE(auth_provider, 'CREDENTIALS');

ALTER TABLE users
ALTER COLUMN auth_provider
SET
  NOT NULL;
