CREATE TABLE IF NOT EXISTS grades (
    uuid CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    grade_value INT NOT NULL UNIQUE,
    sort_order INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO grades (uuid, name, description, grade_value, sort_order)
SELECT UUID(), 'Grade 1', 'Grade dasar untuk level awal.', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM grades WHERE grade_value = 1);

INSERT INTO grades (uuid, name, description, grade_value, sort_order)
SELECT UUID(), 'Grade 2', 'Grade menengah untuk level berkembang.', 2, 2
WHERE NOT EXISTS (SELECT 1 FROM grades WHERE grade_value = 2);

INSERT INTO grades (uuid, name, description, grade_value, sort_order)
SELECT UUID(), 'Grade 3', 'Grade lanjutan untuk level tinggi.', 3, 3
WHERE NOT EXISTS (SELECT 1 FROM grades WHERE grade_value = 3);

ALTER TABLE levels
    ADD COLUMN IF NOT EXISTS grade_uuid CHAR(36) NULL AFTER description;

UPDATE levels l
LEFT JOIN grades g_match ON g_match.grade_value = l.sort_order
LEFT JOIN grades g_default ON g_default.grade_value = 1
SET l.grade_uuid = COALESCE(g_match.uuid, g_default.uuid)
WHERE l.grade_uuid IS NULL;

ALTER TABLE levels
    MODIFY COLUMN grade_uuid CHAR(36) NOT NULL;

ALTER TABLE levels
    ADD CONSTRAINT fk_levels_grade_uuid FOREIGN KEY (grade_uuid) REFERENCES grades(uuid);

CREATE INDEX idx_levels_grade_uuid ON levels(grade_uuid);

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Grade', 'Kelola grade untuk klasifikasi level user.', 7
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'grade');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 1, 1, 1, 1, 1
FROM roles r
JOIN modules m ON LOWER(m.name) = 'grade'
WHERE LOWER(r.name) = 'administrator'
  AND NOT EXISTS (
      SELECT 1
      FROM role_modules rm
      WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
