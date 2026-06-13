SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS role_modules;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS levels;
DROP TABLE IF EXISTS modules;
DROP TABLE IF EXISTS roles;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE roles (
    uuid CHAR(36) PRIMARY KEY,
    code CHAR(36) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE modules (
    uuid CHAR(36) PRIMARY KEY,
    code CHAR(36) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE levels (
    uuid CHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE users (
    uuid CHAR(36) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_uuid CHAR(36) NOT NULL,
    level_uuid CHAR(36) NOT NULL,
    is_super_admin TINYINT(1) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    last_login_at TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role_uuid FOREIGN KEY (role_uuid) REFERENCES roles(uuid),
    CONSTRAINT fk_users_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid)
);

CREATE TABLE role_modules (
    uuid CHAR(36) PRIMARY KEY,
    role_uuid CHAR(36) NOT NULL,
    module_uuid CHAR(36) NOT NULL,
    can_view TINYINT(1) NOT NULL DEFAULT 1,
    can_create TINYINT(1) NOT NULL DEFAULT 0,
    can_update TINYINT(1) NOT NULL DEFAULT 0,
    can_delete TINYINT(1) NOT NULL DEFAULT 0,
    can_export TINYINT(1) NOT NULL DEFAULT 0,
    can_import TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_role_modules_role_module UNIQUE (role_uuid, module_uuid),
    CONSTRAINT fk_role_modules_role_uuid FOREIGN KEY (role_uuid) REFERENCES roles(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_role_modules_module_uuid FOREIGN KEY (module_uuid) REFERENCES modules(uuid) ON DELETE CASCADE
);

SET @admin_role_uuid = UUID();
SET @staff_role_uuid = UUID();
SET @murid_role_uuid = UUID();

INSERT INTO roles (uuid, code, name, description, sort_order) VALUES
(@admin_role_uuid, @admin_role_uuid, 'Administrator', 'Mengelola seluruh data, role, dan pengaturan aplikasi.', 1),
(@staff_role_uuid, @staff_role_uuid, 'Staff Operasional', 'Mengakses modul operasional dan laporan kerja.', 2),
(@murid_role_uuid, @murid_role_uuid, 'Murid', 'Akun murid yang dibuat melalui halaman register.', 3);

SET @dashboard_module_uuid = UUID();
SET @user_module_uuid = UUID();
SET @role_module_uuid = UUID();
SET @laporan_module_uuid = UUID();
SET @pengaturan_module_uuid = UUID();
SET @level_module_uuid = UUID();

INSERT INTO modules (uuid, code, name, description, sort_order) VALUES
(@dashboard_module_uuid, @dashboard_module_uuid, 'Dashboard', 'Ringkasan utama aplikasi dan informasi umum.', 1),
(@user_module_uuid, @user_module_uuid, 'User', 'Manajemen data pengguna, role, status akun, dan penanda super admin.', 2),
(@role_module_uuid, @role_module_uuid, 'Role', 'Mengatur hak akses module yang tampil di navbar berdasarkan role.', 3),
(@laporan_module_uuid, @laporan_module_uuid, 'Laporan', 'Akses laporan dan rekapitulasi data.', 4),
(@pengaturan_module_uuid, @pengaturan_module_uuid, 'Pengaturan', 'Konfigurasi aplikasi dan preferensi sistem.', 5),
(@level_module_uuid, @level_module_uuid, 'Level', 'Kelola level user dan klasifikasi pembinaan.', 6);

SET @basic_level_uuid = UUID();
SET @intermediate_level_uuid = UUID();
SET @pro_level_uuid = UUID();

INSERT INTO levels (uuid, name, description, sort_order) VALUES
(@basic_level_uuid, 'Basic', 'Level dasar untuk peserta pemula.', 1),
(@intermediate_level_uuid, 'Intermediate', 'Level menengah untuk peserta aktif berkembang.', 2),
(@pro_level_uuid, 'Pro', 'Level profesional untuk peserta berpengalaman.', 3);

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import) VALUES
(UUID(), @admin_role_uuid, @dashboard_module_uuid, 1, 1, 1, 1, 1, 1),
(UUID(), @admin_role_uuid, @user_module_uuid, 1, 1, 1, 1, 1, 1),
(UUID(), @admin_role_uuid, @role_module_uuid, 1, 1, 1, 1, 1, 1),
(UUID(), @admin_role_uuid, @laporan_module_uuid, 1, 1, 1, 1, 1, 1),
(UUID(), @admin_role_uuid, @pengaturan_module_uuid, 1, 1, 1, 1, 1, 1),
(UUID(), @admin_role_uuid, @level_module_uuid, 1, 1, 1, 1, 1, 1),
(UUID(), @staff_role_uuid, @dashboard_module_uuid, 1, 0, 0, 0, 0, 0),
(UUID(), @staff_role_uuid, @laporan_module_uuid, 1, 0, 0, 0, 1, 0),
(UUID(), @murid_role_uuid, @dashboard_module_uuid, 1, 0, 0, 0, 0, 0);

INSERT INTO users (uuid, full_name, username, email, password_hash, role_uuid, level_uuid, is_super_admin, is_active, last_login_at)
VALUES (
    UUID(),
    'Super Admin',
    'superadmin',
    'test1@gmail.com',
    '7b8e3e7842e9a24bfe9c801ee25aa4a6fde54fe830003d324e93ddd90acfcbc2',
    @admin_role_uuid,
    @basic_level_uuid,
    1,
    1,
    NULL
);
