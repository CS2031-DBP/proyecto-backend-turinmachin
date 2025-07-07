ALTER TABLE university
ADD COLUMN IF NOT EXISTS name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', name)) STORED;

ALTER TABLE university
ADD COLUMN IF NOT EXISTS short_name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', short_name)) STORED;

CREATE INDEX IF NOT EXISTS university_name_tsv_idx ON university USING GIN (name_tsv);

CREATE INDEX IF NOT EXISTS university_short_name_tsv_idx ON university USING GIN (short_name_tsv);

CREATE INDEX IF NOT EXISTS university_name_trgm_idx ON university USING GIN (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS university_short_name_trgm_idx ON university USING GIN (short_name gin_trgm_ops);

ALTER TABLE degree
ADD COLUMN IF NOT EXISTS name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', name)) STORED;

ALTER TABLE degree
ADD COLUMN IF NOT EXISTS short_name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', short_name)) STORED;

CREATE INDEX IF NOT EXISTS degree_name_tsv_idx ON degree USING GIN (name_tsv);

CREATE INDEX IF NOT EXISTS degree_short_name_tsv_idx ON degree USING GIN (short_name_tsv);

CREATE INDEX IF NOT EXISTS degree_name_trgm_idx ON degree USING GIN (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS degree_short_name_trgm_idx ON degree USING GIN (short_name gin_trgm_ops);
