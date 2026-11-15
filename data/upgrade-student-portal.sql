USE SalesForce;

ALTER TABLE student_details
    ADD COLUMN password_hash VARCHAR(255) NULL,
    ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN parent_mobile VARCHAR(20) NULL,
    ADD UNIQUE KEY uq_student_email (email);

CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT,
    message VARCHAR(2000) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    admin_response VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaints_student FOREIGN KEY (student_id)
        REFERENCES student_details(id) ON DELETE CASCADE,
    CONSTRAINT fk_complaints_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS parent_calls (
    mark_id BIGINT NOT NULL PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    call_sid VARCHAR(64),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent_calls_mark FOREIGN KEY (mark_id)
        REFERENCES student_marks(id) ON DELETE CASCADE
);
