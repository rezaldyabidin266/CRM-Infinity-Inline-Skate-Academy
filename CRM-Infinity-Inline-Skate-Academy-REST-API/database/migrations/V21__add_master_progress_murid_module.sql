CREATE TABLE IF NOT EXISTS progress_templates (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    level_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    name VARCHAR(120) COLLATE utf8mb4_general_ci NOT NULL,
    notes VARCHAR(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_progress_templates_level_name UNIQUE (level_uuid, name),
    CONSTRAINT fk_progress_templates_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS progress_template_items (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    template_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    kode_unit VARCHAR(50) COLLATE utf8mb4_general_ci NOT NULL,
    kompetensi VARCHAR(255) COLLATE utf8mb4_general_ci NOT NULL,
    category VARCHAR(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'FISIK',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_progress_template_items_code UNIQUE (template_uuid, kode_unit),
    CONSTRAINT fk_progress_template_items_template_uuid FOREIGN KEY (template_uuid) REFERENCES progress_templates(uuid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS student_progress_records (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    murid_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    coach_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    template_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    item_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    is_passed TINYINT(1) NOT NULL DEFAULT 0,
    checked_at TIMESTAMP NULL DEFAULT NULL,
    notes VARCHAR(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_progress_item UNIQUE (murid_uuid, item_uuid),
    CONSTRAINT fk_student_progress_murid_uuid FOREIGN KEY (murid_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_student_progress_coach_uuid FOREIGN KEY (coach_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_student_progress_template_uuid FOREIGN KEY (template_uuid) REFERENCES progress_templates(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_student_progress_item_uuid FOREIGN KEY (item_uuid) REFERENCES progress_template_items(uuid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Master Progress Murid', 'Kelola template progress murid per level dan checklist kelulusan oleh coach.', 16
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'master progress murid');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 1, 1, 1, 1, 1
FROM roles r
JOIN modules m ON LOWER(m.name) = 'master progress murid'
WHERE (LOWER(r.name) = 'administrator' OR LOWER(r.name) = 'staff operasional')
  AND NOT EXISTS (
      SELECT 1 FROM role_modules rm WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 0, 1, 0, 0, 0
FROM roles r
JOIN modules m ON LOWER(m.name) = 'master progress murid'
WHERE (LOWER(r.name) LIKE '%coach%' OR LOWER(r.name) LIKE '%pelatih%' OR LOWER(r.name) LIKE '%trainer%' OR LOWER(r.name) LIKE '%instruktur%')
  AND NOT EXISTS (
      SELECT 1 FROM role_modules rm WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
