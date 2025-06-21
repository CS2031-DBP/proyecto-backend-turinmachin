CREATE EXTENSION IF NOT EXISTS pg_trgm;

ALTER TABLE post
ADD COLUMN IF NOT EXISTS content_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', content)) STORED;

CREATE INDEX IF NOT EXISTS content_tsv_idx ON post USING GIN (content_tsv);

CREATE INDEX IF NOT EXISTS post_content_trgm_idx ON post USING GIN (content gin_trgm_ops);
