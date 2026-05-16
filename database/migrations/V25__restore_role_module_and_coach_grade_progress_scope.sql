INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Role', 'Mengatur hak akses module yang tampil di navbar berdasarkan role.', 3
WHERE NOT EXISTS (
    SELECT 1
    FROM modules
    WHERE LOWER(name) IN ('role', 'role module')
);

UPDATE modules
SET name = 'Role',
    description = 'Mengatur hak akses module yang tampil di navbar berdasarkan role.',
    sort_order = 3
WHERE LOWER(name) = 'role module';

UPDATE modules
SET sort_order = 3
WHERE LOWER(name) = 'role';

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 1, 1, 1, 1, 1
FROM roles r
JOIN modules m ON LOWER(m.name) = 'role'
WHERE (LOWER(r.name) LIKE '%admin%' OR LOWER(r.name) LIKE '%administrator%')
  AND NOT EXISTS (
      SELECT 1
      FROM role_modules rm
      WHERE rm.role_uuid = r.uuid
        AND rm.module_uuid = m.uuid
  );
