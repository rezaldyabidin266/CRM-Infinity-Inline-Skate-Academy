SET @admin_role_uuid = (
    SELECT uuid
    FROM roles
    WHERE name = 'Administrator'
    LIMIT 1
);

SET @admin_role_uuid = COALESCE(@admin_role_uuid, UUID());

INSERT IGNORE INTO roles (uuid, code, name, description, sort_order)
VALUES (@admin_role_uuid, @admin_role_uuid, 'Administrator', 'Mengelola seluruh data, role, dan pengaturan aplikasi.', 1);

INSERT INTO role_modules (uuid, role_uuid, module_uuid)
SELECT UUID(), @admin_role_uuid, m.uuid
FROM modules m
WHERE NOT EXISTS (
    SELECT 1
    FROM role_modules rm
    WHERE rm.role_uuid = @admin_role_uuid
      AND rm.module_uuid = m.uuid
);

UPDATE users
SET role_uuid = @admin_role_uuid,
    is_super_admin = 1,
    is_active = 1
WHERE username = 'superadmin'
   OR email = 'test1@gmail.com'
   OR is_super_admin = 1;
