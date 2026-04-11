CREATE TABLE IF NOT EXISTS roles (
    code VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS modules (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS role_modules (
    role_code VARCHAR(30) NOT NULL,
    module_code VARCHAR(50) NOT NULL,
    PRIMARY KEY (role_code, module_code),
    CONSTRAINT fk_role_modules_role FOREIGN KEY (role_code) REFERENCES roles(code) ON DELETE CASCADE,
    CONSTRAINT fk_role_modules_module FOREIGN KEY (module_code) REFERENCES modules(code) ON DELETE CASCADE
);

INSERT IGNORE INTO roles (code, name, description, sort_order) VALUES
('ADMIN', 'Administrator', 'Mengelola seluruh data, role, dan pengaturan aplikasi.', 1),
('STAFF', 'Staff Operasional', 'Mengakses modul operasional dan laporan kerja.', 2),
('USER', 'Pengguna Umum', 'Mengakses modul dasar sesuai kebutuhan pengguna.', 3);

INSERT IGNORE INTO modules (code, name, description, sort_order) VALUES
('DASHBOARD', 'Dashboard', 'Ringkasan utama aplikasi dan informasi umum.', 1),
('KELOLA_USER', 'Kelola User', 'Manajemen data pengguna, role, dan status akun.', 2),
('LAPORAN', 'Laporan', 'Akses laporan dan rekapitulasi data.', 3),
('PENGATURAN', 'Pengaturan', 'Konfigurasi aplikasi dan preferensi sistem.', 4);

INSERT IGNORE INTO role_modules (role_code, module_code) VALUES
('ADMIN', 'DASHBOARD'),
('ADMIN', 'KELOLA_USER'),
('ADMIN', 'LAPORAN'),
('ADMIN', 'PENGATURAN'),
('STAFF', 'DASHBOARD'),
('STAFF', 'LAPORAN'),
('USER', 'DASHBOARD');
