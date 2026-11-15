USE SalesForce;

CREATE TABLE IF NOT EXISTS scheduled_parent_calls (
    mark_id BIGINT NOT NULL PRIMARY KEY,
    scheduled_at_epoch_ms BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_scheduled_parent_calls_due (status, scheduled_at_epoch_ms),
    CONSTRAINT fk_scheduled_parent_calls_mark FOREIGN KEY (mark_id)
        REFERENCES student_marks(id) ON DELETE CASCADE
);
