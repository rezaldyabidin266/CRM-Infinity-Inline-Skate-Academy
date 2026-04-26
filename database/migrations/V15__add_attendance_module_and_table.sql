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
    CONSTRAINT fk_attendance_coach_uuid FOREIGN KEY (coach_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_murid_uuid FOREIGN KEY (murid_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT uq_attendance_unique_entry UNIQUE (coach_uuid, murid_uuid, tanggal_absensi, pertemuan_ke)
);

CREATE INDEX idx_attendance_month ON attendance_records(tanggal_absensi);
CREATE INDEX idx_attendance_coach ON attendance_records(coach_uuid);
CREATE INDEX idx_attendance_murid ON attendance_records(murid_uuid);

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
