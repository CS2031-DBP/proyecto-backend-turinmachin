CREATE TABLE user_token (
  user_id UUID PRIMARY KEY REFERENCES users (id),
  value VARCHAR(255) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);

ALTER TABLE users
ADD COLUMN password_reset_token UUID REFERENCES user_token (user_id);
