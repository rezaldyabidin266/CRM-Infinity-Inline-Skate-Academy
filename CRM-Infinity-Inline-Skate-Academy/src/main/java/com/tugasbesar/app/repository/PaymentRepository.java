package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.CoachPaymentSummary;
import com.tugasbesar.app.model.CoachSalaryPaymentRecord;
import com.tugasbesar.app.model.GradeCoachPaymentRate;
import com.tugasbesar.app.model.LevelPaymentConfig;
import com.tugasbesar.app.model.StudentPaymentRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentRepository {
    public List<LevelPaymentConfig> findLevelPaymentConfigs() {
        String sql = "SELECT c.uuid, c.level_uuid, l.name AS level_name, l.grade_uuid, g.name AS grade_name, c.monthly_spp "
                + "FROM levels l "
                + "JOIN grades g ON g.uuid = l.grade_uuid "
                + "LEFT JOIN level_payment_configs c ON c.level_uuid = l.uuid "
                + "ORDER BY g.grade_value ASC, l.sort_order ASC, l.name ASC";
        List<LevelPaymentConfig> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                LevelPaymentConfig row = new LevelPaymentConfig();
                row.setUuid(resultSet.getString("uuid"));
                row.setLevelUuid(resultSet.getString("level_uuid"));
                row.setLevelName(resultSet.getString("level_name"));
                row.setGradeUuid(resultSet.getString("grade_uuid"));
                row.setGradeName(resultSet.getString("grade_name"));
                row.setMonthlySpp(resultSet.getBigDecimal("monthly_spp"));
                rows.add(row);
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil konfigurasi SPP level.", exception);
        }
    }

    public void saveLevelPaymentConfig(String levelUuid, BigDecimal monthlySpp) {
        String sql = "INSERT INTO level_payment_configs (uuid, level_uuid, monthly_spp) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE monthly_spp = VALUES(monthly_spp)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, levelUuid);
            statement.setBigDecimal(3, monthlySpp);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan konfigurasi SPP level.", exception);
        }
    }

    public List<GradeCoachPaymentRate> findGradeCoachPaymentRates() {
        String sql = "SELECT r.uuid, g.uuid AS grade_uuid, g.name AS grade_name, g.grade_value, "
                + "COALESCE(r.monthly_rate, 0) AS monthly_rate, "
                + "(SELECT COUNT(*) FROM users u "
                + " JOIN roles ro ON ro.uuid = u.role_uuid "
                + " WHERE u.grade_uuid = g.uuid AND u.is_super_admin = 0 "
                + "   AND (LOWER(ro.name) LIKE '%pelatih%' OR LOWER(ro.name) LIKE '%coach%' OR LOWER(ro.name) LIKE '%trainer%' OR LOWER(ro.name) LIKE '%instruktur%')) AS coach_count "
                + "FROM grades g "
                + "LEFT JOIN grade_coach_payment_rates r ON r.grade_uuid = g.uuid "
                + "ORDER BY g.grade_value ASC, g.sort_order ASC, g.name ASC";
        List<GradeCoachPaymentRate> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                GradeCoachPaymentRate row = new GradeCoachPaymentRate();
                row.setUuid(resultSet.getString("uuid"));
                row.setGradeUuid(resultSet.getString("grade_uuid"));
                row.setGradeName(resultSet.getString("grade_name"));
                row.setGradeValue(resultSet.getInt("grade_value"));
                row.setCoachCount(resultSet.getInt("coach_count"));
                row.setMonthlyRate(resultSet.getBigDecimal("monthly_rate"));
                rows.add(row);
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil rate pembayaran coach.", exception);
        }
    }

    public void saveGradeCoachPaymentRate(String gradeUuid, BigDecimal monthlyRate) {
        String sql = "INSERT INTO grade_coach_payment_rates (uuid, grade_uuid, monthly_rate) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE monthly_rate = VALUES(monthly_rate)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, gradeUuid);
            statement.setBigDecimal(3, monthlyRate);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan rate pembayaran coach.", exception);
        }
    }

    public List<CoachPaymentSummary> findCoachPaymentSummaries() {
        String sql = "SELECT u.uuid AS coach_uuid, u.full_name AS coach_name, g.name AS grade_name, COALESCE(r.monthly_rate, 0) AS monthly_rate "
                + "FROM users u "
                + "JOIN roles ro ON ro.uuid = u.role_uuid "
                + "LEFT JOIN grades g ON g.uuid = u.grade_uuid "
                + "LEFT JOIN grade_coach_payment_rates r ON r.grade_uuid = u.grade_uuid "
                + "WHERE u.is_super_admin = 0 "
                + "  AND (LOWER(ro.name) LIKE '%pelatih%' OR LOWER(ro.name) LIKE '%coach%' OR LOWER(ro.name) LIKE '%trainer%' OR LOWER(ro.name) LIKE '%instruktur%') "
                + "ORDER BY g.grade_value ASC, u.full_name ASC";
        List<CoachPaymentSummary> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                CoachPaymentSummary row = new CoachPaymentSummary();
                row.setCoachUuid(resultSet.getString("coach_uuid"));
                row.setCoachName(resultSet.getString("coach_name"));
                row.setGradeName(resultSet.getString("grade_name"));
                row.setMonthlyRate(resultSet.getBigDecimal("monthly_rate"));
                rows.add(row);
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil ringkasan pembayaran coach.", exception);
        }
    }

    public void syncStudentPaymentsForPeriod(int year, int month) {
        String sql = "INSERT INTO student_payments (uuid, murid_uuid, grade_uuid, level_uuid, payment_year, payment_month, spp_amount, is_paid, paid_at, notes) "
                + "SELECT UUID(), u.uuid, u.grade_uuid, u.level_uuid, ?, ?, COALESCE(c.monthly_spp, 0), 0, NULL, '' "
                + "FROM users u "
                + "JOIN roles r ON r.uuid = u.role_uuid "
                + "LEFT JOIN level_payment_configs c ON c.level_uuid = u.level_uuid "
                + "WHERE u.is_super_admin = 0 "
                + "  AND (LOWER(r.name) LIKE '%murid%' OR LOWER(r.name) LIKE '%student%' OR LOWER(r.name) LIKE '%siswa%' OR LOWER(r.name) LIKE '%trial%') "
                + "ON DUPLICATE KEY UPDATE grade_uuid = VALUES(grade_uuid), level_uuid = VALUES(level_uuid), spp_amount = VALUES(spp_amount)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, year);
            statement.setInt(2, month);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal sinkronisasi data pembayaran murid.", exception);
        }
    }

    public List<StudentPaymentRecord> findStudentPayments(int year, int month, String levelUuid, Boolean paidOnly) {
        StringBuilder sql = new StringBuilder(
                "SELECT sp.uuid, sp.murid_uuid, u.full_name AS murid_name, u.username, g.name AS grade_name, l.name AS level_name, "
                        + "sp.payment_year, sp.payment_month, sp.spp_amount, sp.is_paid, sp.paid_at, sp.notes "
                        + "FROM student_payments sp "
                        + "JOIN users u ON u.uuid = sp.murid_uuid "
                        + "LEFT JOIN grades g ON g.uuid = sp.grade_uuid "
                        + "LEFT JOIN levels l ON l.uuid = sp.level_uuid "
                        + "WHERE sp.payment_year = ? AND sp.payment_month = ?");
        List<Object> params = new ArrayList<>();
        params.add(year);
        params.add(month);
        if (levelUuid != null && !levelUuid.trim().isEmpty()) {
            sql.append(" AND sp.level_uuid = ?");
            params.add(levelUuid.trim());
        }
        if (paidOnly != null) {
            sql.append(" AND sp.is_paid = ?");
            params.add(paidOnly);
        }
        sql.append(" ORDER BY g.name ASC, l.name ASC, u.full_name ASC");

        List<StudentPaymentRecord> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < params.size(); index++) {
                Object value = params.get(index);
                if (value instanceof Integer) {
                    statement.setInt(index + 1, (Integer) value);
                } else if (value instanceof Boolean) {
                    statement.setBoolean(index + 1, (Boolean) value);
                } else {
                    statement.setString(index + 1, String.valueOf(value));
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapStudentPayment(resultSet));
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil pembayaran murid.", exception);
        }
    }

    public void updateStudentPayment(String paymentUuid, boolean paid, String notes) {
        String sql = "UPDATE student_payments SET is_paid = ?, paid_at = ?, notes = ? WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, paid);
            statement.setTimestamp(2, paid ? Timestamp.valueOf(LocalDateTime.now()) : null);
            statement.setString(3, notes == null ? "" : notes.trim());
            statement.setString(4, paymentUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui pembayaran murid.", exception);
        }
    }

    public void syncCoachSalaryPaymentsForPeriod(int year, int month) {
        String sql = "INSERT INTO coach_salary_payments (uuid, coach_uuid, grade_uuid, payment_year, payment_month, salary_amount, is_paid, paid_at, notes) "
                + "SELECT UUID(), u.uuid, u.grade_uuid, ?, ?, COALESCE(r.monthly_rate, 0), 0, NULL, '' "
                + "FROM users u "
                + "JOIN roles ro ON ro.uuid = u.role_uuid "
                + "LEFT JOIN grade_coach_payment_rates r ON r.grade_uuid = u.grade_uuid "
                + "WHERE u.is_super_admin = 0 "
                + "  AND (LOWER(ro.name) LIKE '%pelatih%' OR LOWER(ro.name) LIKE '%coach%' OR LOWER(ro.name) LIKE '%trainer%' OR LOWER(ro.name) LIKE '%instruktur%') "
                + "ON DUPLICATE KEY UPDATE grade_uuid = VALUES(grade_uuid), salary_amount = VALUES(salary_amount)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, year);
            statement.setInt(2, month);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal sinkronisasi pembayaran gaji coach.", exception);
        }
    }

    public List<CoachSalaryPaymentRecord> findCoachSalaryPayments(int year, int month, String gradeName, Boolean paidOnly) {
        StringBuilder sql = new StringBuilder(
                "SELECT cp.uuid, cp.coach_uuid, u.full_name AS coach_name, g.name AS grade_name, cp.payment_year, cp.payment_month, "
                        + "cp.salary_amount, cp.is_paid, cp.paid_at, cp.notes "
                        + "FROM coach_salary_payments cp "
                        + "JOIN users u ON u.uuid = cp.coach_uuid "
                        + "LEFT JOIN grades g ON g.uuid = cp.grade_uuid "
                        + "WHERE cp.payment_year = ? AND cp.payment_month = ?");
        List<Object> params = new ArrayList<>();
        params.add(year);
        params.add(month);
        if (gradeName != null && !gradeName.trim().isEmpty()) {
            sql.append(" AND LOWER(g.name) = LOWER(?)");
            params.add(gradeName.trim());
        }
        if (paidOnly != null) {
            sql.append(" AND cp.is_paid = ?");
            params.add(paidOnly);
        }
        sql.append(" ORDER BY g.name ASC, u.full_name ASC");

        List<CoachSalaryPaymentRecord> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < params.size(); index++) {
                Object value = params.get(index);
                if (value instanceof Integer) {
                    statement.setInt(index + 1, (Integer) value);
                } else if (value instanceof Boolean) {
                    statement.setBoolean(index + 1, (Boolean) value);
                } else {
                    statement.setString(index + 1, String.valueOf(value));
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CoachSalaryPaymentRecord row = new CoachSalaryPaymentRecord();
                    row.setUuid(resultSet.getString("uuid"));
                    row.setCoachUuid(resultSet.getString("coach_uuid"));
                    row.setCoachName(resultSet.getString("coach_name"));
                    row.setGradeName(resultSet.getString("grade_name"));
                    row.setPaymentYear(resultSet.getInt("payment_year"));
                    row.setPaymentMonth(resultSet.getInt("payment_month"));
                    row.setSalaryAmount(resultSet.getBigDecimal("salary_amount"));
                    row.setPaid(resultSet.getBoolean("is_paid"));
                    Timestamp paidAt = resultSet.getTimestamp("paid_at");
                    if (paidAt != null) {
                        row.setPaidAt(paidAt.toLocalDateTime());
                    }
                    row.setNotes(resultSet.getString("notes"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil pembayaran gaji coach.", exception);
        }
    }

    public void updateCoachSalaryPayment(String paymentUuid, boolean paid, String notes) {
        String sql = "UPDATE coach_salary_payments SET is_paid = ?, paid_at = ?, notes = ? WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, paid);
            statement.setTimestamp(2, paid ? Timestamp.valueOf(LocalDateTime.now()) : null);
            statement.setString(3, notes == null ? "" : notes.trim());
            statement.setString(4, paymentUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui pembayaran gaji coach.", exception);
        }
    }

    private StudentPaymentRecord mapStudentPayment(ResultSet resultSet) throws SQLException {
        StudentPaymentRecord row = new StudentPaymentRecord();
        row.setUuid(resultSet.getString("uuid"));
        row.setMuridUuid(resultSet.getString("murid_uuid"));
        row.setMuridName(resultSet.getString("murid_name"));
        row.setUsername(resultSet.getString("username"));
        row.setGradeName(resultSet.getString("grade_name"));
        row.setLevelName(resultSet.getString("level_name"));
        row.setPaymentYear(resultSet.getInt("payment_year"));
        row.setPaymentMonth(resultSet.getInt("payment_month"));
        row.setSppAmount(resultSet.getBigDecimal("spp_amount"));
        row.setPaid(resultSet.getBoolean("is_paid"));
        Timestamp paidAt = resultSet.getTimestamp("paid_at");
        if (paidAt != null) {
            row.setPaidAt(paidAt.toLocalDateTime());
        }
        row.setNotes(resultSet.getString("notes"));
        return row;
    }
}
