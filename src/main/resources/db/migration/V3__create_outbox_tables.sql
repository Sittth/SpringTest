CREATE TABLE test.notifications(
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient text NOT NULL,
    message text NOT NULL,
    status text NOT NULL DEFAULT 'PENDING'
        CONSTRAINT notifications_status_check
        CHECK ( status IN ('PENDING', 'SENT', 'FAILED') ),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    is_deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TRIGGER trigger_notifications_updated_at
BEFORE UPDATE ON notifications
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TABLE test.notification_outbox(
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    notification_id uuid NOT NULL REFERENCES notifications(id),
    payload text NOT NULL,
    status text NOT NULL DEFAULT 'PENDING'
        CONSTRAINT notification_outbox_status_check
        CHECK (status IN ('PENDING', 'FAILED')),
    attempts int NOT NULL DEFAULT 0,
    locked_until timestamptz,
    next_retry_at timestamptz DEFAULT now(),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_notification_outbox_pending
    ON test.notification_outbox (next_retry_at)
    WHERE status = 'PENDING';