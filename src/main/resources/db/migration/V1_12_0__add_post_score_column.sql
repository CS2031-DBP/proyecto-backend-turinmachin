ALTER TABLE post
ADD COLUMN score INT;

UPDATE post P
SET
  score = (
    SELECT
      COALESCE(SUM(value), 0)
    FROM
      post_vote PV
    WHERE
      PV.post_id = P.id
  );

ALTER TABLE post
ALTER COLUMN score
SET
  NOT NULL;
