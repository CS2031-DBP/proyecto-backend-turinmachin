CREATE INDEX users_username_trgm_idx ON users USING GIN (username gin_trgm_ops);

CREATE INDEX users_display_name_trgm_idx ON users USING GIN (display_name gin_trgm_ops);
