INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Level', 'Kelola level user dan klasifikasi pembinaan.', 6
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'level');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 1, 1, 1, 1, 1
FROM roles r
JOIN modules m ON LOWER(m.name) = 'level'
WHERE LOWER(r.name) = 'administrator'
  AND NOT EXISTS (
      SELECT 1
      FROM role_modules rm
      WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
