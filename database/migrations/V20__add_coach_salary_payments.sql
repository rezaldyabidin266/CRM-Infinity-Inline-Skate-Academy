CREATE TABLE IF NOT EXISTS coach_salary_payments (
    uuid CHAR(36) COLLATE utf8mb4_general_ci PRIMARY KEY,
    coach_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    grade_uuid CHAR(36) COLLATE utf8mb4_general_ci NOT NULL,
    payment_year INT NOT NULL,
    payment_month TINYINT NOT NULL,
    salary_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    is_paid TINYINT(1) NOT NULL DEFAULT 0,
    paid_at TIMESTAMP NULL DEFAULT NULL,
    notes VARCHAR(255) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_coach_salary_payments_period UNIQUE (coach_uuid, payment_year, payment_month),
    CONSTRAINT fk_coach_salary_payments_coach_uuid FOREIGN KEY (coach_uuid) REFERENCES users(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_coach_salary_payments_grade_uuid FOREIGN KEY (grade_uuid) REFERENCES grades(uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
