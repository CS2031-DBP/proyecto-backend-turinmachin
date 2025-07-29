CREATE EXTENSION IF NOT EXISTS pg_trgm;

ALTER TABLE post
ADD COLUMN content_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', content)) STORED;

CREATE INDEX content_tsv_idx ON post USING GIN (content_tsv);

CREATE INDEX post_content_trgm_idx ON post USING GIN (content gin_trgm_ops);
