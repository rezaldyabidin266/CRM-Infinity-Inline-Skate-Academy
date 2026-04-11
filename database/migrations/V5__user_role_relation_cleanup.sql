SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'users'
              AND index_name = 'idx_users_role_uuid'
        ),
        'SELECT 1',
        'ALTER TABLE users ADD INDEX idx_users_role_uuid (role_uuid)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE users u
JOIN roles r ON r.uuid = u.role_uuid
SET u.role = r.code
WHERE u.role <> r.code
   OR u.role IS NULL
   OR u.role = '';
