ALTER TABLE role_modules
    ADD COLUMN IF NOT EXISTS can_view TINYINT(1) NOT NULL DEFAULT 1 AFTER module_uuid,
    ADD COLUMN IF NOT EXISTS can_create TINYINT(1) NOT NULL DEFAULT 0 AFTER can_view,
    ADD COLUMN IF NOT EXISTS can_update TINYINT(1) NOT NULL DEFAULT 0 AFTER can_create,
    ADD COLUMN IF NOT EXISTS can_delete TINYINT(1) NOT NULL DEFAULT 0 AFTER can_update,
    ADD COLUMN IF NOT EXISTS can_export TINYINT(1) NOT NULL DEFAULT 0 AFTER can_delete,
    ADD COLUMN IF NOT EXISTS can_import TINYINT(1) NOT NULL DEFAULT 0 AFTER can_export;

UPDATE role_modules rm
JOIN roles r ON r.uuid = rm.role_uuid
SET rm.can_view = 1,
    rm.can_create = 1,
    rm.can_update = 1,
    rm.can_delete = 1,
    rm.can_export = 1,
    rm.can_import = 1
WHERE r.name = 'Administrator';

UPDATE role_modules
SET can_view = 1
WHERE can_view = 0
  AND can_create = 0
  AND can_update = 0
  AND can_delete = 0
  AND can_export = 0
  AND can_import = 0;
