ALTER TABLE test.books
    ADD COLUMN attempts int NOT NULL DEFAULT 0,
    ADD COLUMN next_retry_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE test.books DROP CONSTRAINT books_metadata_status_check;

ALTER TABLE test.books
    ADD CONSTRAINT books_metadata_status_check
        CHECK (metadata_status IN ('PENDING', 'CONFIRMED', 'FAILED'));