CREATE TABLE chat_subscription (
  id UUID NOT NULL PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users (id),
  endpoint VARCHAR(255) NOT NULL UNIQUE,
  auth VARCHAR(255) NOT NULL,
  key VARCHAR(255) NOT NULL
);

CREATE INDEX idx_chat_subscription_user_id ON chat_subscription (user_id);
