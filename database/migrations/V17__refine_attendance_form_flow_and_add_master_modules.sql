ALTER TABLE attendance_forms
    ADD COLUMN level_uuid CHAR(36) NULL AFTER coach_uuid,
    ADD COLUMN attendance_date DATE NULL AFTER level_uuid,
    ADD COLUMN pertemuan_ke TINYINT NULL AFTER attendance_date;

UPDATE attendance_forms af
LEFT JOIN attendance_form_levels afl ON afl.attendance_form_uuid = af.uuid
SET af.level_uuid = COALESCE(af.level_uuid, afl.level_uuid)
WHERE af.level_uuid IS NULL;

UPDATE attendance_forms
SET attendance_date = COALESCE(attendance_date, STR_TO_DATE(CONCAT(period_year, '-', LPAD(period_month, 2, '0'), '-01'), '%Y-%m-%d'))
WHERE attendance_date IS NULL;

UPDATE attendance_forms
SET pertemuan_ke = COALESCE(pertemuan_ke, 1)
WHERE pertemuan_ke IS NULL;

ALTER TABLE attendance_forms
    MODIFY COLUMN level_uuid CHAR(36) COLLATE utf8mb4_general_ci NULL;

SET @fk_attendance_forms_level_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_forms'
      AND constraint_name = 'fk_attendance_forms_level_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_forms_level_uuid_exists = 0,
    'ALTER TABLE attendance_forms ADD CONSTRAINT fk_attendance_forms_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid) ON DELETE CASCADE',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_forms_slot_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_forms'
      AND index_name = 'idx_attendance_forms_slot'
);

SET @sql = IF(
    @idx_attendance_forms_slot_exists = 0,
    'CREATE UNIQUE INDEX idx_attendance_forms_slot ON attendance_forms(coach_uuid, level_uuid, attendance_date, pertemuan_ke)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_forms_level_date_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_forms'
      AND index_name = 'idx_attendance_forms_level_date'
);

SET @sql = IF(
    @idx_attendance_forms_level_date_exists = 0,
    'CREATE INDEX idx_attendance_forms_level_date ON attendance_forms(level_uuid, attendance_date, pertemuan_ke)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Master Murid', 'Master data murid.', 11
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'master murid');

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Master Coach', 'Master data coach.', 12
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'master coach');

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Master Peralatan', 'Master data peralatan.', 13
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'master peralatan');

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Master Absensi', 'Master form absensi coach.', 14
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'master absensi');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 1, 1, 1, 1, 1
FROM roles r
JOIN modules m ON LOWER(m.name) IN ('master murid', 'master coach', 'master peralatan', 'master absensi')
WHERE LOWER(r.name) = 'administrator'
  AND NOT EXISTS (
      SELECT 1 FROM role_modules rm WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
