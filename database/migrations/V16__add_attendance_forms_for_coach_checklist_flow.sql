CREATE TABLE IF NOT EXISTS attendance_forms (
    uuid CHAR(36) PRIMARY KEY,
    coach_uuid CHAR(36) NOT NULL,
    period_year INT NOT NULL,
    period_month TINYINT NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    notes VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE attendance_forms
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE attendance_forms
    MODIFY COLUMN uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    MODIFY COLUMN coach_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL;

SET @fk_attendance_forms_coach_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_forms'
      AND constraint_name = 'fk_attendance_forms_coach_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_forms_coach_uuid_exists = 0,
    'ALTER TABLE attendance_forms ADD CONSTRAINT fk_attendance_forms_coach_uuid FOREIGN KEY (coach_uuid) REFERENCES users(uuid) ON DELETE CASCADE',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_forms_period_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_forms'
      AND index_name = 'idx_attendance_forms_period'
);

SET @sql = IF(
    @idx_attendance_forms_period_exists = 0,
    'CREATE INDEX idx_attendance_forms_period ON attendance_forms(period_year, period_month, coach_uuid)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS attendance_form_levels (
    uuid CHAR(36) PRIMARY KEY,
    attendance_form_uuid CHAR(36) NOT NULL,
    level_uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_form_levels UNIQUE (attendance_form_uuid, level_uuid)
);

ALTER TABLE attendance_form_levels
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE attendance_form_levels
    MODIFY COLUMN uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    MODIFY COLUMN attendance_form_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    MODIFY COLUMN level_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL;

SET @fk_attendance_form_levels_form_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_form_levels'
      AND constraint_name = 'fk_attendance_form_levels_form_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_form_levels_form_uuid_exists = 0,
    'ALTER TABLE attendance_form_levels ADD CONSTRAINT fk_attendance_form_levels_form_uuid FOREIGN KEY (attendance_form_uuid) REFERENCES attendance_forms(uuid) ON DELETE CASCADE',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_attendance_form_levels_level_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_form_levels'
      AND constraint_name = 'fk_attendance_form_levels_level_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_form_levels_level_uuid_exists = 0,
    'ALTER TABLE attendance_form_levels ADD CONSTRAINT fk_attendance_form_levels_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid) ON DELETE CASCADE',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE attendance_records
    ADD COLUMN attendance_form_uuid CHAR(36) NULL AFTER uuid,
    ADD COLUMN level_uuid CHAR(36) NULL AFTER murid_uuid;

ALTER TABLE attendance_records
    MODIFY COLUMN attendance_form_uuid CHAR(36) COLLATE utf8mb4_general_ci NULL,
    MODIFY COLUMN level_uuid CHAR(36) COLLATE utf8mb4_general_ci NULL;

SET @fk_attendance_records_form_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND constraint_name = 'fk_attendance_records_form_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_records_form_uuid_exists = 0,
    'ALTER TABLE attendance_records ADD CONSTRAINT fk_attendance_records_form_uuid FOREIGN KEY (attendance_form_uuid) REFERENCES attendance_forms(uuid) ON DELETE SET NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_attendance_records_level_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND constraint_name = 'fk_attendance_records_level_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_records_level_uuid_exists = 0,
    'ALTER TABLE attendance_records ADD CONSTRAINT fk_attendance_records_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid) ON DELETE SET NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_records_form_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND index_name = 'idx_attendance_records_form_uuid'
);

SET @sql = IF(
    @idx_attendance_records_form_uuid_exists = 0,
    'CREATE INDEX idx_attendance_records_form_uuid ON attendance_records(attendance_form_uuid)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_records_level_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND index_name = 'idx_attendance_records_level_uuid'
);

SET @sql = IF(
    @idx_attendance_records_level_uuid_exists = 0,
    'CREATE INDEX idx_attendance_records_level_uuid ON attendance_records(level_uuid)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
