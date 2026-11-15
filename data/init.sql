CREATE DATABASE IF NOT EXISTS SalesForce;
USE SalesForce;

CREATE TABLE IF NOT EXISTS admin_details (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS student_details (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255),
    department VARCHAR(255),
    city VARCHAR(255),
    password_hash VARCHAR(255),
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    parent_mobile VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS student_marks (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    marks DECIMAL(5, 2) NOT NULL,
    CONSTRAINT chk_student_marks_range CHECK (marks >= 0 AND marks <= 100),
    CONSTRAINT uq_student_subject UNIQUE (student_id, subject_id),
    CONSTRAINT fk_student_marks_student FOREIGN KEY (student_id)
        REFERENCES student_details(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_marks_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(id) ON DELETE CASCADE
);

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

INSERT INTO admin_details (user_name, email, password)
VALUES ('Admin', 'admin@gmail.com', 'admin123')
ON DUPLICATE KEY UPDATE email = VALUES(email);
