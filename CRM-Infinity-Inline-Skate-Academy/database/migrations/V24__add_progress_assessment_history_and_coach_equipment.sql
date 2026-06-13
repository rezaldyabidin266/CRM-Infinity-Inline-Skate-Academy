CREATE TABLE IF NOT EXISTS student_progress_assessments (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    murid_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    coach_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    template_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    name VARCHAR(120) COLLATE utf8mb4_general_ci NOT NULL,
    assessment_date DATE NOT NULL,
    notes VARCHAR(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_progress_assessments_murid_uuid FOREIGN KEY (murid_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_student_progress_assessments_coach_uuid FOREIGN KEY (coach_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_student_progress_assessments_template_uuid FOREIGN KEY (template_uuid) REFERENCES progress_templates(uuid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET @sql = IF(
    (
        SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'student_progress_records'
          AND COLUMN_NAME = 'assessment_uuid'
    ) = 0,
    'ALTER TABLE student_progress_records ADD COLUMN assessment_uuid CHAR(36) COLLATE utf8mb4_general_ci NULL AFTER uuid',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO student_progress_assessments (uuid, murid_uuid, coach_uuid, template_uuid, name, assessment_date, notes)
SELECT UUID(), spr.murid_uuid, spr.coach_uuid, spr.template_uuid, 'Migrasi Riwayat Lama',
       COALESCE(DATE(MIN(spr.checked_at)), CURDATE()), ''
FROM student_progress_records spr
LEFT JOIN student_progress_assessments spa
    ON spa.murid_uuid = spr.murid_uuid
   AND spa.coach_uuid = spr.coach_uuid
   AND spa.template_uuid = spr.template_uuid
WHERE spa.uuid IS NULL
GROUP BY spr.murid_uuid, spr.coach_uuid, spr.template_uuid;

UPDATE student_progress_records spr
JOIN student_progress_assessments spa
    ON spa.murid_uuid = spr.murid_uuid
   AND spa.coach_uuid = spr.coach_uuid
   AND spa.template_uuid = spr.template_uuid
SET spr.assessment_uuid = spa.uuid
WHERE spr.assessment_uuid IS NULL;

ALTER TABLE student_progress_records
MODIFY COLUMN assessment_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL;

SET @sql = IF(
    (
        SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'student_progress_records'
          AND INDEX_NAME = 'idx_student_progress_murid_uuid'
    ) = 0,
    'ALTER TABLE student_progress_records ADD INDEX idx_student_progress_murid_uuid (murid_uuid)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (
        SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'student_progress_records'
          AND INDEX_NAME = 'uq_student_progress_item'
    ) > 0,
    'ALTER TABLE student_progress_records DROP INDEX uq_student_progress_item',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (
        SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'student_progress_records'
          AND INDEX_NAME = 'uq_student_progress_assessment_item'
    ) = 0,
    'ALTER TABLE student_progress_records ADD CONSTRAINT uq_student_progress_assessment_item UNIQUE (assessment_uuid, item_uuid)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (
        SELECT COUNT(*)
        FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'student_progress_records'
          AND CONSTRAINT_NAME = 'fk_student_progress_assessment_uuid'
    ) = 0,
    'ALTER TABLE student_progress_records ADD CONSTRAINT fk_student_progress_assessment_uuid FOREIGN KEY (assessment_uuid) REFERENCES student_progress_assessments(uuid) ON DELETE CASCADE',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Peralatan Coach', 'Lihat data peralatan untuk kebutuhan coach tanpa akses master.', 17
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'peralatan coach');

DELETE rm
FROM role_modules rm
JOIN roles r ON r.uuid = rm.role_uuid
JOIN modules m ON m.uuid = rm.module_uuid
WHERE LOWER(m.name) = 'master peralatan'
  AND (LOWER(r.name) LIKE '%coach%' OR LOWER(r.name) LIKE '%pelatih%' OR LOWER(r.name) LIKE '%trainer%' OR LOWER(r.name) LIKE '%instruktur%');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 0, 0, 0, 0, 0
FROM roles r
JOIN modules m ON LOWER(m.name) = 'peralatan coach'
WHERE (LOWER(r.name) LIKE '%coach%' OR LOWER(r.name) LIKE '%pelatih%' OR LOWER(r.name) LIKE '%trainer%' OR LOWER(r.name) LIKE '%instruktur%')
  AND NOT EXISTS (
      SELECT 1 FROM role_modules rm WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
