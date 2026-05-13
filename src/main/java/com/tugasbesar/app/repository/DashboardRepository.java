package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.AdminDashboardData;
import com.tugasbesar.app.model.CoachDashboardData;
import com.tugasbesar.app.model.DashboardActivityItem;
import com.tugasbesar.app.model.DashboardMetric;
import com.tugasbesar.app.model.StudentDashboardData;
import com.tugasbesar.app.model.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardRepository {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public DashboardRepository() {
        this.paymentRepository = new PaymentRepository();
        this.userRepository = new UserRepository();
    }

    public AdminDashboardData loadAdminDashboardData() {
        LocalDate today = LocalDate.now();
        syncCurrentPayments(today);

        AdminDashboardData data = new AdminDashboardData();
        List<DashboardMetric> metrics = new ArrayList<>();
        metrics.add(new DashboardMetric("Murid Aktif", String.valueOf(countBySql("SELECT COUNT(*) FROM users u JOIN roles r ON r.uuid = u.role_uuid WHERE u.is_active = 1 AND u.is_super_admin = 0 AND (LOWER(r.name) LIKE '%murid%' OR LOWER(r.name) LIKE '%student%' OR LOWER(r.name) LIKE '%siswa%' OR LOWER(r.name) LIKE '%trial%')"))));
        metrics.add(new DashboardMetric("Coach Aktif", String.valueOf(countBySql("SELECT COUNT(*) FROM users u JOIN roles r ON r.uuid = u.role_uuid WHERE u.is_active = 1 AND u.is_super_admin = 0 AND (LOWER(r.name) LIKE '%pelatih%' OR LOWER(r.name) LIKE '%coach%' OR LOWER(r.name) LIKE '%trainer%' OR LOWER(r.name) LIKE '%instruktur%')"))));
        metrics.add(new DashboardMetric("Level / Class", String.valueOf(countBySql("SELECT COUNT(*) FROM levels"))));
        metrics.add(new DashboardMetric("Form Hari Ini", String.valueOf(countBySql("SELECT COUNT(*) FROM attendance_forms WHERE attendance_date = CURDATE() AND is_active = 1"))));
        metrics.add(new DashboardMetric("SPP Lunas Bulan Ini", String.valueOf(countBySql("SELECT COUNT(*) FROM student_payments WHERE payment_year = YEAR(CURDATE()) AND payment_month = MONTH(CURDATE()) AND is_paid = 1"))));
        metrics.add(new DashboardMetric("SPP Belum Lunas", String.valueOf(countBySql("SELECT COUNT(*) FROM student_payments WHERE payment_year = YEAR(CURDATE()) AND payment_month = MONTH(CURDATE()) AND is_paid = 0"))));
        metrics.add(new DashboardMetric("Gaji Coach Dibayar", String.valueOf(countBySql("SELECT COUNT(*) FROM coach_salary_payments WHERE payment_year = YEAR(CURDATE()) AND payment_month = MONTH(CURDATE()) AND is_paid = 1"))));
        metrics.add(new DashboardMetric("Gaji Coach Pending", String.valueOf(countBySql("SELECT COUNT(*) FROM coach_salary_payments WHERE payment_year = YEAR(CURDATE()) AND payment_month = MONTH(CURDATE()) AND is_paid = 0"))));
        data.setMetrics(metrics);

        data.setRecentAttendanceForms(loadActivities(
                "SELECT u.full_name AS title, l.name AS subtitle, af.attendance_date AS date_value, CONCAT('Pertemuan ', af.pertemuan_ke) AS meta_text, CASE WHEN af.is_active = 1 THEN 'Aktif' ELSE 'Nonaktif' END AS status_text "
                        + "FROM attendance_forms af "
                        + "JOIN users u ON u.uuid = af.coach_uuid "
                        + "JOIN levels l ON l.uuid = af.level_uuid "
                        + "ORDER BY af.attendance_date DESC, af.updated_at DESC LIMIT 6"));

        data.setRecentStudentPayments(loadActivities(
                "SELECT u.full_name AS title, l.name AS subtitle, NULL AS date_value, CONCAT('SPP ', sp.payment_month, '/', sp.payment_year) AS meta_text, CASE WHEN sp.is_paid = 1 THEN 'Lunas' ELSE 'Belum Bayar' END AS status_text "
                        + "FROM student_payments sp "
                        + "JOIN users u ON u.uuid = sp.murid_uuid "
                        + "LEFT JOIN levels l ON l.uuid = sp.level_uuid "
                        + "WHERE sp.payment_year = YEAR(CURDATE()) AND sp.payment_month = MONTH(CURDATE()) "
                        + "ORDER BY sp.updated_at DESC, u.full_name ASC LIMIT 6"));

        data.setRecentCoachPayments(loadActivities(
                "SELECT u.full_name AS title, g.name AS subtitle, NULL AS date_value, CONCAT('Gaji ', cp.payment_month, '/', cp.payment_year) AS meta_text, CASE WHEN cp.is_paid = 1 THEN 'Dibayar' ELSE 'Pending' END AS status_text "
                        + "FROM coach_salary_payments cp "
                        + "JOIN users u ON u.uuid = cp.coach_uuid "
                        + "LEFT JOIN grades g ON g.uuid = cp.grade_uuid "
                        + "WHERE cp.payment_year = YEAR(CURDATE()) AND cp.payment_month = MONTH(CURDATE()) "
                        + "ORDER BY cp.updated_at DESC, u.full_name ASC LIMIT 6"));
        return data;
    }

    public CoachDashboardData loadCoachDashboardData(User user) {
        LocalDate today = LocalDate.now();
        syncCurrentPayments(today);

        CoachDashboardData data = new CoachDashboardData();
        List<DashboardMetric> metrics = new ArrayList<>();
        metrics.add(new DashboardMetric("Grade Dipegang", safeValue(user.getGradeName(), "-")));
        metrics.add(new DashboardMetric("Class", safeValue(user.getLevelName(), "-")));
        metrics.add(new DashboardMetric("Murid Dalam Class", String.valueOf(userRepository.findMuridUsersForCoachLevelAndGrade(user.getUuid()).size())));
        metrics.add(new DashboardMetric("Form Aktif Hari Ini", String.valueOf(countBySql("SELECT COUNT(*) FROM attendance_forms WHERE coach_uuid = ? AND attendance_date = CURDATE() AND is_active = 1", user.getUuid()))));
        metrics.add(new DashboardMetric("Absensi Bulan Ini", String.valueOf(countBySql("SELECT COUNT(*) FROM attendance_records WHERE coach_uuid = ? AND YEAR(tanggal_absensi) = YEAR(CURDATE()) AND MONTH(tanggal_absensi) = MONTH(CURDATE())", user.getUuid()))));
        metrics.add(new DashboardMetric("Status Gaji Bulan Ini", findCoachSalaryStatus(user.getUuid(), today)));
        data.setMetrics(metrics);

        data.setActiveForms(loadActivities(
                "SELECT l.name AS title, CONCAT('Coach: ', u.full_name) AS subtitle, af.attendance_date AS date_value, CONCAT('Pertemuan ', af.pertemuan_ke) AS meta_text, CASE WHEN af.is_active = 1 THEN 'Aktif' ELSE 'Nonaktif' END AS status_text "
                        + "FROM attendance_forms af "
                        + "JOIN users u ON u.uuid = af.coach_uuid "
                        + "JOIN levels l ON l.uuid = af.level_uuid "
                        + "WHERE af.coach_uuid = ? "
                        + "ORDER BY af.attendance_date DESC, af.pertemuan_ke ASC LIMIT 6", user.getUuid()));

        data.setRecentAttendance(loadActivities(
                "SELECT m.full_name AS title, lv.name AS subtitle, ar.tanggal_absensi AS date_value, CONCAT('Pertemuan ', ar.pertemuan_ke) AS meta_text, ar.status_absensi AS status_text "
                        + "FROM attendance_records ar "
                        + "JOIN users m ON m.uuid = ar.murid_uuid "
                        + "LEFT JOIN levels lv ON lv.uuid = ar.level_uuid "
                        + "WHERE ar.coach_uuid = ? "
                        + "ORDER BY ar.tanggal_absensi DESC, ar.updated_at DESC LIMIT 6", user.getUuid()));

        List<DashboardActivityItem> notes = new ArrayList<>();
        notes.add(note("Absensi", "Isi form aktif hari ini dari menu Absensi.", "Prioritas", "Action"));
        notes.add(note("Pembayaran", "Status gaji bulanan bisa dipantau dari Master Pembayaran.", "Informasi", "Info"));
        notes.add(note("Class", "Coach hanya melihat murid sesuai grade/class yang dipegang.", "Akses", "Scope"));
        data.setQuickNotes(notes);
        return data;
    }

    public StudentDashboardData loadStudentDashboardData(User user) {
        LocalDate today = LocalDate.now();
        syncCurrentPayments(today);

        StudentDashboardData data = new StudentDashboardData();
        List<DashboardMetric> metrics = new ArrayList<>();
        metrics.add(new DashboardMetric("Grade", safeValue(user.getGradeName(), "-")));
        metrics.add(new DashboardMetric("Class", safeValue(user.getLevelName(), "-")));
        metrics.add(new DashboardMetric("Hadir Bulan Ini", String.valueOf(countBySql("SELECT COUNT(*) FROM attendance_records WHERE murid_uuid = ? AND YEAR(tanggal_absensi) = YEAR(CURDATE()) AND MONTH(tanggal_absensi) = MONTH(CURDATE()) AND LOWER(status_absensi) = 'hadir'", user.getUuid()))));
        metrics.add(new DashboardMetric("Total Absensi", String.valueOf(countBySql("SELECT COUNT(*) FROM attendance_records WHERE murid_uuid = ? AND YEAR(tanggal_absensi) = YEAR(CURDATE()) AND MONTH(tanggal_absensi) = MONTH(CURDATE())", user.getUuid()))));
        metrics.add(new DashboardMetric("SPP Bulan Ini", findStudentPaymentStatus(user.getUuid(), today)));
        data.setMetrics(metrics);

        data.setRecentAttendance(loadActivities(
                "SELECT CONCAT('Absensi ', COALESCE(lv.name, '-')) AS title, ar.status_absensi AS subtitle, ar.tanggal_absensi AS date_value, CONCAT('Pertemuan ', ar.pertemuan_ke) AS meta_text, c.full_name AS status_text "
                        + "FROM attendance_records ar "
                        + "JOIN users c ON c.uuid = ar.coach_uuid "
                        + "LEFT JOIN levels lv ON lv.uuid = ar.level_uuid "
                        + "WHERE ar.murid_uuid = ? "
                        + "ORDER BY ar.tanggal_absensi DESC, ar.updated_at DESC LIMIT 6", user.getUuid()));

        data.setRecentPayments(loadActivities(
                "SELECT CONCAT('SPP ', sp.payment_month, '/', sp.payment_year) AS title, COALESCE(l.name, '-') AS subtitle, NULL AS date_value, CONCAT('Nominal ', COALESCE(sp.spp_amount, 0)) AS meta_text, CASE WHEN sp.is_paid = 1 THEN 'Lunas' ELSE 'Belum Bayar' END AS status_text "
                        + "FROM student_payments sp "
                        + "LEFT JOIN levels l ON l.uuid = sp.level_uuid "
                        + "WHERE sp.murid_uuid = ? "
                        + "ORDER BY sp.payment_year DESC, sp.payment_month DESC LIMIT 6", user.getUuid()));

        List<DashboardActivityItem> notes = new ArrayList<>();
        notes.add(note("Coach", "Lihat coach dan class aktif Anda dari riwayat absensi.", "Info", "Class"));
        notes.add(note("Pembayaran", "Status SPP bulanan muncul otomatis setelah admin update pembayaran.", "Info", "SPP"));
        notes.add(note("Kehadiran", "Pantau kehadiran bulan berjalan dari dashboard pribadi.", "Info", "Attendance"));
        data.setQuickNotes(notes);
        return data;
    }

    private DashboardActivityItem note(String title, String subtitle, String meta, String status) {
        DashboardActivityItem item = new DashboardActivityItem();
        item.setTitle(title);
        item.setSubtitle(subtitle);
        item.setMeta(meta);
        item.setStatus(status);
        return item;
    }

    private void syncCurrentPayments(LocalDate today) {
        paymentRepository.syncStudentPaymentsForPeriod(today.getYear(), today.getMonthValue());
        paymentRepository.syncCoachSalaryPaymentsForPeriod(today.getYear(), today.getMonthValue());
    }

    private String findCoachSalaryStatus(String coachUuid, LocalDate today) {
        String sql = "SELECT is_paid FROM coach_salary_payments WHERE coach_uuid = ? AND payment_year = ? AND payment_month = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, coachUuid);
            statement.setInt(2, today.getYear());
            statement.setInt(3, today.getMonthValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_paid") ? "Sudah Dibayar" : "Belum Dibayar";
                }
            }
            return "Belum Ada Data";
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil status gaji coach.", exception);
        }
    }

    private String findStudentPaymentStatus(String muridUuid, LocalDate today) {
        String sql = "SELECT is_paid FROM student_payments WHERE murid_uuid = ? AND payment_year = ? AND payment_month = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, muridUuid);
            statement.setInt(2, today.getYear());
            statement.setInt(3, today.getMonthValue());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_paid") ? "Lunas" : "Belum Bayar";
                }
            }
            return "Belum Ada Tagihan";
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil status pembayaran murid.", exception);
        }
    }

    private int countBySql(String sql, Object... params) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParams(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data dashboard.", exception);
        }
    }

    private List<DashboardActivityItem> loadActivities(String sql, Object... params) {
        List<DashboardActivityItem> items = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindParams(statement, params);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    DashboardActivityItem item = new DashboardActivityItem();
                    item.setTitle(safeValue(resultSet.getString("title"), "-"));
                    item.setSubtitle(safeValue(resultSet.getString("subtitle"), "-"));
                    Date dateValue = resultSet.getDate("date_value");
                    String metaText = resultSet.getString("meta_text");
                    if (dateValue != null) {
                        item.setMeta(DATE_FORMAT.format(dateValue.toLocalDate()) + " • " + safeValue(metaText, "-"));
                    } else {
                        item.setMeta(safeValue(metaText, "-"));
                    }
                    item.setStatus(safeValue(resultSet.getString("status_text"), "-"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil aktivitas dashboard.", exception);
        }
    }

    private void bindParams(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object value = params[i];
            if (value instanceof Integer) {
                statement.setInt(i + 1, (Integer) value);
            } else {
                statement.setString(i + 1, String.valueOf(value));
            }
        }
    }

    private String safeValue(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
