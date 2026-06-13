CREATE TABLE IF NOT EXISTS attendance_records (
    uuid CHAR(36) PRIMARY KEY,
    coach_uuid CHAR(36) NOT NULL,
    murid_uuid CHAR(36) NOT NULL,
    tanggal_absensi DATE NOT NULL,
    pertemuan_ke TINYINT NOT NULL,
    status_absensi VARCHAR(20) NOT NULL DEFAULT 'Hadir',
    catatan VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_attendance_unique_entry UNIQUE (coach_uuid, murid_uuid, tanggal_absensi, pertemuan_ke)
);

ALTER TABLE attendance_records
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE attendance_records
    MODIFY COLUMN uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    MODIFY COLUMN coach_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    MODIFY COLUMN murid_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL;

SET @fk_attendance_coach_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND constraint_name = 'fk_attendance_coach_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_coach_uuid_exists = 0,
    'ALTER TABLE attendance_records ADD CONSTRAINT fk_attendance_coach_uuid FOREIGN KEY (coach_uuid) REFERENCES users(uuid) ON DELETE CASCADE',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_attendance_murid_uuid_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND constraint_name = 'fk_attendance_murid_uuid'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql = IF(
    @fk_attendance_murid_uuid_exists = 0,
    'ALTER TABLE attendance_records ADD CONSTRAINT fk_attendance_murid_uuid FOREIGN KEY (murid_uuid) REFERENCES users(uuid) ON DELETE CASCADE',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_month_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND index_name = 'idx_attendance_month'
);

SET @sql = IF(
    @idx_attendance_month_exists = 0,
    'CREATE INDEX idx_attendance_month ON attendance_records(tanggal_absensi)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_coach_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND index_name = 'idx_attendance_coach'
);

SET @sql = IF(
    @idx_attendance_coach_exists = 0,
    'CREATE INDEX idx_attendance_coach ON attendance_records(coach_uuid)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_attendance_murid_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'attendance_records'
      AND index_name = 'idx_attendance_murid'
);

SET @sql = IF(
    @idx_attendance_murid_exists = 0,
    'CREATE INDEX idx_attendance_murid ON attendance_records(murid_uuid)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Absensi', 'Kelola absensi murid per pertemuan oleh coach.', 8
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'absensi');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 1, 1, 1, 1, 1
FROM roles r
JOIN modules m ON LOWER(m.name) = 'absensi'
WHERE (LOWER(r.name) = 'administrator'
       OR LOWER(r.name) = 'staff operasional'
       OR LOWER(r.name) LIKE '%coach%'
       OR LOWER(r.name) LIKE '%pelatih%'
       OR LOWER(r.name) LIKE '%trainer%'
       OR LOWER(r.name) LIKE '%instruktur%')
  AND NOT EXISTS (
      SELECT 1
      FROM role_modules rm
      WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
