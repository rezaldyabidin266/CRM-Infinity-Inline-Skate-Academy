INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Checklist Progress Murid', 'Halaman coach untuk mengisi checklist progress kenaikan level murid.', 17
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'checklist progress murid');

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Progress Saya', 'Halaman murid untuk melihat progress checklist miliknya.', 18
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'progress saya');

DELETE rm
FROM role_modules rm
JOIN roles r ON r.uuid = rm.role_uuid
JOIN modules m ON m.uuid = rm.module_uuid
WHERE LOWER(m.name) = 'master progress murid'
  AND (LOWER(r.name) LIKE '%coach%' OR LOWER(r.name) LIKE '%pelatih%' OR LOWER(r.name) LIKE '%trainer%' OR LOWER(r.name) LIKE '%instruktur%');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 0, 1, 0, 0, 0
FROM roles r
JOIN modules m ON LOWER(m.name) = 'checklist progress murid'
WHERE (LOWER(r.name) LIKE '%coach%' OR LOWER(r.name) LIKE '%pelatih%' OR LOWER(r.name) LIKE '%trainer%' OR LOWER(r.name) LIKE '%instruktur%')
  AND NOT EXISTS (
      SELECT 1 FROM role_modules rm WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 0, 0, 0, 0, 0
FROM roles r
JOIN modules m ON LOWER(m.name) = 'progress saya'
WHERE (LOWER(r.name) LIKE '%murid%' OR LOWER(r.name) LIKE '%student%' OR LOWER(r.name) LIKE '%siswa%' OR LOWER(r.name) LIKE '%trial%')
  AND NOT EXISTS (
      SELECT 1 FROM role_modules rm WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
