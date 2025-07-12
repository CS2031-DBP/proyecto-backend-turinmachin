ALTER TABLE university
ADD COLUMN name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', name)) STORED;

ALTER TABLE university
ADD COLUMN short_name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', short_name)) STORED;

CREATE INDEX university_name_tsv_idx ON university USING GIN (name_tsv);

CREATE INDEX university_short_name_tsv_idx ON university USING GIN (short_name_tsv);

CREATE INDEX university_name_trgm_idx ON university USING GIN (name gin_trgm_ops);

CREATE INDEX university_short_name_trgm_idx ON university USING GIN (short_name gin_trgm_ops);

ALTER TABLE degree
ADD COLUMN name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', name)) STORED;

ALTER TABLE degree
ADD COLUMN short_name_tsv tsvector GENERATED ALWAYS AS (to_tsvector ('spanish', short_name)) STORED;

CREATE INDEX degree_name_tsv_idx ON degree USING GIN (name_tsv);

CREATE INDEX degree_short_name_tsv_idx ON degree USING GIN (short_name_tsv);

CREATE INDEX degree_name_trgm_idx ON degree USING GIN (name gin_trgm_ops);

CREATE INDEX degree_short_name_trgm_idx ON degree USING GIN (short_name gin_trgm_ops);
