ALTER TABLE users
    ADD COLUMN IF NOT EXISTS is_super_admin TINYINT(1) NOT NULL DEFAULT 0 AFTER role_uuid;

UPDATE modules
SET name = 'User',
    description = 'Manajemen data pengguna, role, status akun, dan penanda super admin.'
WHERE code = 'KELOLA_USER';

INSERT IGNORE INTO modules (code, name, description, sort_order) VALUES
('ROLE_MODULE', 'Role Module', 'Melihat relasi role dengan module yang diizinkan di sistem.', 3);

UPDATE modules
SET sort_order = 4
WHERE code = 'LAPORAN';

UPDATE modules
SET sort_order = 5
WHERE code = 'PENGATURAN';

INSERT IGNORE INTO role_modules (role_code, module_code, role_uuid, module_uuid, uuid)
SELECT 'ADMIN', 'ROLE_MODULE', r.uuid, m.uuid, UUID()
FROM roles r
JOIN modules m ON m.code = 'ROLE_MODULE'
WHERE r.code = 'ADMIN';

INSERT INTO users (uuid, full_name, username, email, password_hash, role, role_uuid, is_super_admin, is_active, last_login_at)
SELECT UUID(), 'Super Admin', 'superadmin', 'test1@gmail.com',
       '7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2',
       r.code, r.uuid, 1, 1, NULL
FROM roles r
WHERE r.code = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM users
      WHERE email = 'test1@gmail.com'
         OR username = 'superadmin'
         OR is_super_admin = 1
  );
