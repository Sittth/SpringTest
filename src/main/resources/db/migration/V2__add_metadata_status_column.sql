ALTER TABLE test.books
    ADD COLUMN IF NOT EXISTS metadata_status text NOT NULL DEFAULT 'PENDING'
    CHECK (metadata_status IN ('PENDING', 'CONFIRMED'));

ALTER TABLE test.books
    ADD COLUMN attempts int NOT NULL DEFAULT 0,
    ADD COLUMN next_retry_at timestamptz NOT NULL DEFAULT now();

ALTER TABLE test.books
DROP CONSTRAINT books_metadata_status_check;

ALTER TABLE test.books
    ADD CONSTRAINT books_metadata_status_check
        CHECK (metadata_status IN ('PENDING', 'CONFIRMED', 'FAILED'));

CREATE TABLE test.shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE test.cache_invalidation_queue (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cache_name text NOT NULL,
    cache_key text NOT NULL,
    attempts int NOT NULL DEFAULT 0,
    next_retry_at timestamptz NOT NULL DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE test.books
    ADD COLUMN IF NOT EXISTS publisher text,
    ADD COLUMN IF NOT EXISTS price NUMERIC(10, 2);

ALTER TABLE test.users
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.profiles
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.authors
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.books
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.courses
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.students
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE test.books
    ADD COLUMN IF NOT EXISTS locked_until timestamptz;

ALTER TABLE test.cache_invalidation_queue
    ADD COLUMN IF NOT EXISTS locked_until timestamptz,
    ADD COLUMN IF NOT EXISTS status text NOT NULL DEFAULT 'PENDING',
    ADD COLUMN IF NOT EXISTS last_error text;

ALTER TABLE test.cache_invalidation_queue
    ADD CONSTRAINT cache_invalidation_queue_status_check
        CHECK (status IN ('PENDING', 'FAILED'));

CREATE INDEX IF NOT EXISTS idx_cache_invalidation_queue_pending
    ON test.cache_invalidation_queue (next_retry_at)
    WHERE status = 'PENDING';

ALTER TABLE test.cache_invalidation_queue
    ALTER COLUMN next_retry_at DROP NOT NULL;

UPDATE test.cache_invalidation_queue
SET next_retry_at = NULL
WHERE status = 'FAILED';

ALTER TABLE test.books
    ALTER COLUMN next_retry_at DROP NOT NULL;

UPDATE test.books
SET next_retry_at = NULL
WHERE metadata_status IN ('FAILED', 'CONFIRMED');