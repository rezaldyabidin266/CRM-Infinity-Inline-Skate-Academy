CREATE TABLE IF NOT EXISTS level_payment_configs (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    level_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    monthly_spp DECIMAL(12, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_level_payment_configs_level_uuid UNIQUE (level_uuid),
    CONSTRAINT fk_level_payment_configs_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS grade_coach_payment_rates (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    grade_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    monthly_rate DECIMAL(12, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_grade_coach_payment_rates_grade_uuid UNIQUE (grade_uuid),
    CONSTRAINT fk_grade_coach_payment_rates_grade_uuid FOREIGN KEY (grade_uuid) REFERENCES grades(uuid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS student_payments (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    murid_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    grade_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    level_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    payment_year INT NOT NULL,
    payment_month TINYINT NOT NULL,
    spp_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    is_paid TINYINT(1) NOT NULL DEFAULT 0,
    paid_at TIMESTAMP NULL DEFAULT NULL,
    notes VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_payments_period UNIQUE (murid_uuid, payment_year, payment_month),
    CONSTRAINT fk_student_payments_murid_uuid FOREIGN KEY (murid_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_student_payments_grade_uuid FOREIGN KEY (grade_uuid) REFERENCES grades(uuid),
    CONSTRAINT fk_student_payments_level_uuid FOREIGN KEY (level_uuid) REFERENCES levels(uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO level_payment_configs (uuid, level_uuid, monthly_spp)
SELECT UUID(), l.uuid, 0
FROM levels l
WHERE NOT EXISTS (
    SELECT 1 FROM level_payment_configs c WHERE c.level_uuid = l.uuid
);

INSERT INTO grade_coach_payment_rates (uuid, grade_uuid, monthly_rate)
SELECT UUID(), g.uuid, 0
FROM grades g
WHERE NOT EXISTS (
    SELECT 1 FROM grade_coach_payment_rates r WHERE r.grade_uuid = g.uuid
);

INSERT INTO modules (uuid, code, name, description, sort_order)
SELECT UUID(), UUID(), 'Master Pembayaran', 'Kelola SPP per level, rate coach per grade, dan checklist pembayaran murid.', 15
WHERE NOT EXISTS (SELECT 1 FROM modules WHERE LOWER(name) = 'master pembayaran');

INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import)
SELECT UUID(), r.uuid, m.uuid, 1, 1, 1, 1, 1, 1
FROM roles r
JOIN modules m ON LOWER(m.name) = 'master pembayaran'
WHERE (LOWER(r.name) = 'administrator' OR LOWER(r.name) = 'staff operasional')
  AND NOT EXISTS (
      SELECT 1 FROM role_modules rm WHERE rm.role_uuid = r.uuid AND rm.module_uuid = m.uuid
  );
