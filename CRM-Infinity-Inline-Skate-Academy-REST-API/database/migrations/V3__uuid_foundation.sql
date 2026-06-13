ALTER TABLE roles
    ADD COLUMN IF NOT EXISTS uuid CHAR(36) NULL FIRST;

UPDATE roles
SET uuid = UUID()
WHERE uuid IS NULL OR uuid = '';

ALTER TABLE roles
    MODIFY COLUMN uuid CHAR(36) NOT NULL;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'roles'
              AND constraint_name = 'uq_roles_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE roles ADD CONSTRAINT uq_roles_uuid UNIQUE (uuid)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE modules
    ADD COLUMN IF NOT EXISTS uuid CHAR(36) NULL FIRST;

UPDATE modules
SET uuid = UUID()
WHERE uuid IS NULL OR uuid = '';

ALTER TABLE modules
    MODIFY COLUMN uuid CHAR(36) NOT NULL;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'modules'
              AND constraint_name = 'uq_modules_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE modules ADD CONSTRAINT uq_modules_uuid UNIQUE (uuid)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS uuid CHAR(36) NULL AFTER id,
    ADD COLUMN IF NOT EXISTS role_uuid CHAR(36) NULL AFTER role;

UPDATE users
JOIN roles r ON r.code = users.role
SET users.uuid = COALESCE(NULLIF(users.uuid, ''), UUID()),
    users.role_uuid = r.uuid
WHERE users.uuid IS NULL
   OR users.uuid = ''
   OR users.role_uuid IS NULL;

ALTER TABLE users
    MODIFY COLUMN uuid CHAR(36) NOT NULL,
    MODIFY COLUMN role_uuid CHAR(36) NOT NULL;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'users'
              AND constraint_name = 'uq_users_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE users ADD CONSTRAINT uq_users_uuid UNIQUE (uuid)'
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
              AND table_name = 'users'
              AND constraint_name = 'fk_users_role_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE users ADD CONSTRAINT fk_users_role_uuid FOREIGN KEY (role_uuid) REFERENCES roles(uuid)'
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
              AND table_name = 'users'
              AND constraint_name = 'fk_users_role_code'
        ),
        'SELECT 1',
        'ALTER TABLE users ADD CONSTRAINT fk_users_role_code FOREIGN KEY (role) REFERENCES roles(code)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE role_modules
    ADD COLUMN IF NOT EXISTS uuid CHAR(36) NULL FIRST,
    ADD COLUMN IF NOT EXISTS role_uuid CHAR(36) NULL AFTER role_code,
    ADD COLUMN IF NOT EXISTS module_uuid CHAR(36) NULL AFTER module_code;

UPDATE role_modules rm
JOIN roles r ON r.code = rm.role_code
JOIN modules m ON m.code = rm.module_code
SET rm.uuid = COALESCE(rm.uuid, UUID()),
    rm.role_uuid = r.uuid,
    rm.module_uuid = m.uuid
WHERE rm.uuid IS NULL
   OR rm.uuid = ''
   OR rm.role_uuid IS NULL
   OR rm.module_uuid IS NULL;

ALTER TABLE role_modules
    MODIFY COLUMN uuid CHAR(36) NOT NULL,
    MODIFY COLUMN role_uuid CHAR(36) NOT NULL,
    MODIFY COLUMN module_uuid CHAR(36) NOT NULL;

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.table_constraints
            WHERE table_schema = DATABASE()
              AND table_name = 'role_modules'
              AND constraint_name = 'uq_role_modules_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE role_modules ADD CONSTRAINT uq_role_modules_uuid UNIQUE (uuid)'
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
              AND constraint_name = 'fk_role_modules_role_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE role_modules ADD CONSTRAINT fk_role_modules_role_uuid FOREIGN KEY (role_uuid) REFERENCES roles(uuid) ON DELETE CASCADE'
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
              AND constraint_name = 'fk_role_modules_module_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE role_modules ADD CONSTRAINT fk_role_modules_module_uuid FOREIGN KEY (module_uuid) REFERENCES modules(uuid) ON DELETE CASCADE'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
