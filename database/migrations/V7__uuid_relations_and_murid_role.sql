INSERT IGNORE INTO roles (uuid, code, name, description, sort_order)
VALUES (UUID(), 'MURID', 'Murid', 'Akun murid yang dibuat melalui halaman register.', 4);

INSERT INTO role_modules (uuid, role_code, module_code, role_uuid, module_uuid)
SELECT UUID(), r.code, m.code, r.uuid, m.uuid
FROM roles r
JOIN modules m ON m.code = 'DASHBOARD'
WHERE r.code = 'MURID'
  AND NOT EXISTS (
      SELECT 1
      FROM role_modules rm
      WHERE rm.role_uuid = r.uuid
        AND rm.module_uuid = m.uuid
  );

UPDATE users u
JOIN roles r ON r.code = u.role
SET u.role_uuid = r.uuid
WHERE u.role_uuid IS NULL
   OR u.role_uuid = ''
   OR u.role_uuid <> r.uuid;

UPDATE role_modules rm
JOIN roles r ON r.code = rm.role_code
JOIN modules m ON m.code = rm.module_code
SET rm.role_uuid = r.uuid,
    rm.module_uuid = m.uuid
WHERE rm.role_uuid IS NULL
   OR rm.role_uuid = ''
   OR rm.module_uuid IS NULL
   OR rm.module_uuid = '';

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'users'
              AND constraint_name = 'fk_users_role_code'
        ),
        'ALTER TABLE users DROP FOREIGN KEY fk_users_role_code',
        'SELECT 1'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'role_modules'
              AND constraint_name = 'fk_role_modules_role'
        ),
        'ALTER TABLE role_modules DROP FOREIGN KEY fk_role_modules_role',
        'SELECT 1'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'role_modules'
              AND constraint_name = 'fk_role_modules_module'
        ),
        'ALTER TABLE role_modules DROP FOREIGN KEY fk_role_modules_module',
        'SELECT 1'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'role_modules'
              AND constraint_type = 'PRIMARY KEY'
        ),
        'ALTER TABLE role_modules DROP PRIMARY KEY',
        'SELECT 1'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE roles
    MODIFY COLUMN code VARCHAR(36) NOT NULL;

ALTER TABLE users
    MODIFY COLUMN role VARCHAR(36) NOT NULL DEFAULT 'MURID';

ALTER TABLE role_modules
    MODIFY COLUMN role_code VARCHAR(36) NULL,
    MODIFY COLUMN module_code VARCHAR(50) NULL;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'role_modules'
              AND index_name = 'uq_role_modules_role_uuid_module_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE role_modules ADD CONSTRAINT uq_role_modules_role_uuid_module_uuid UNIQUE (role_uuid, module_uuid)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
