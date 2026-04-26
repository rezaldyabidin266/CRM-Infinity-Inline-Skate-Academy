CREATE TABLE IF NOT EXISTS attendance_forms (
    uuid CHAR(36) PRIMARY KEY,
    coach_uuid CHAR(36) NOT NULL,
    period_year INT NOT NULL,
    period_month TINYINT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    notes VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_forms_coach_uuid FOREIGN KEY (coach_uuid) REFERENCES users(uuid) ON DELETE CASCADE
);

CREATE INDEX idx_attendance_forms_period ON attendance_forms(period_year, period_month, coach_uuid);

CREATE TABLE IF NOT EXISTS attendance_form_levels (
    uuid CHAR(36) PRIMARY KEY,
    attendance_form_uuid CHAR(36) NOT NULL,
    level_uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_form_levels UNIQUE (attendance_form_uuid, level_uuid),
    CONSTRAINT fk_attendance_form_levels_form_uuid FOREIGN KEY (attendance_form_uuid) REFERENCES attendance_forms(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_form_levels_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid) ON DELETE CASCADE
);

ALTER TABLE attendance_records
    ADD COLUMN IF NOT EXISTS attendance_form_uuid CHAR(36) NULL AFTER uuid,
    ADD COLUMN IF NOT EXISTS level_uuid CHAR(36) NULL AFTER murid_uuid;

ALTER TABLE attendance_records
    ADD CONSTRAINT fk_attendance_records_form_uuid FOREIGN KEY (attendance_form_uuid) REFERENCES attendance_forms(uuid) ON DELETE SET NULL;

ALTER TABLE attendance_records
    ADD CONSTRAINT fk_attendance_records_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid) ON DELETE SET NULL;

CREATE INDEX idx_attendance_records_form_uuid ON attendance_records(attendance_form_uuid);
CREATE INDEX idx_attendance_records_level_uuid ON attendance_records(level_uuid);
