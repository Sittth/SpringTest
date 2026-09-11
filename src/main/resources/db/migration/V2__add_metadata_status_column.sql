ALTER TABLE test.books
    ADD COLUMN IF NOT EXISTS metadata_status text NOT NULL DEFAULT 'PENDING'
        CHECK (metadata_status IN ('PENDING', 'CONFIRMED'));