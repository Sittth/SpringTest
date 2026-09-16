CREATE TABLE test.cache_invalidation_queue (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    cache_name text NOT NULL,
    cache_key text NOT NULL,
    attempts int NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now()
);