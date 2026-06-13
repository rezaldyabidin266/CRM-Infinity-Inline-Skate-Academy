CREATE TABLE IF NOT EXISTS levels (
    uuid CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO levels (uuid, name, description, sort_order)
SELECT UUID(), 'Basic', 'Level dasar untuk peserta pemula.', 1
WHERE NOT EXISTS (SELECT 1 FROM levels WHERE LOWER(name) = 'basic');

INSERT INTO levels (uuid, name, description, sort_order)
SELECT UUID(), 'Intermediate', 'Level menengah untuk peserta aktif berkembang.', 2
WHERE NOT EXISTS (SELECT 1 FROM levels WHERE LOWER(name) = 'intermediate');

INSERT INTO levels (uuid, name, description, sort_order)
SELECT UUID(), 'Pro', 'Level profesional untuk peserta berpengalaman.', 3
WHERE NOT EXISTS (SELECT 1 FROM levels WHERE LOWER(name) = 'pro');

ALTER TABLE users
    ADD COLUMN level_uuid CHAR(36) NULL AFTER role_uuid;

UPDATE users
SET level_uuid = (SELECT uuid FROM levels WHERE LOWER(name) = 'basic' LIMIT 1)
WHERE level_uuid IS NULL;

ALTER TABLE users
    MODIFY COLUMN level_uuid CHAR(36) NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT fk_users_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid);

CREATE INDEX idx_users_level_uuid ON users(level_uuid);
