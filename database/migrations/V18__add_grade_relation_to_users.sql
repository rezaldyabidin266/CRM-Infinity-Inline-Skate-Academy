ALTER TABLE users
    ADD COLUMN grade_uuid CHAR(36) NULL AFTER role_uuid;

UPDATE users u
LEFT JOIN levels l ON l.uuid = u.level_uuid
SET u.grade_uuid = COALESCE(
    l.grade_uuid,
    (
        SELECT g.uuid
        FROM grades g
        ORDER BY g.grade_value ASC, g.sort_order ASC, g.name ASC
        LIMIT 1
    )
)
WHERE u.grade_uuid IS NULL OR u.grade_uuid = '';

ALTER TABLE users
    MODIFY COLUMN grade_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL;

ALTER TABLE users
    ADD INDEX idx_users_grade_uuid (grade_uuid);

ALTER TABLE users
    ADD CONSTRAINT fk_users_grade_uuid FOREIGN KEY (grade_uuid) REFERENCES grades(uuid);
