package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.AttendanceChecklistItem;
import com.tugasbesar.app.model.AttendanceForm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttendanceFormRepository {
    public List<AttendanceForm> findAllForms() {
        String sql = "SELECT af.uuid, af.coach_uuid, u.full_name AS coach_name, af.level_uuid, l.name AS level_name, "
                + "af.attendance_date, af.pertemuan_ke, af.is_active, af.notes "
                + "FROM attendance_forms af "
                + "JOIN users u ON u.uuid = af.coach_uuid "
                + "JOIN levels l ON l.uuid = af.level_uuid "
                + "ORDER BY af.attendance_date DESC, af.pertemuan_ke ASC, u.full_name ASC, l.name ASC";
        List<AttendanceForm> forms = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                forms.add(mapForm(resultSet));
            }
            return forms;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil master absensi.", exception);
        }
    }

    public List<AttendanceForm> findByFilters(String coachUuid, Date attendanceDate, String levelUuid, Integer pertemuanKe) {
        StringBuilder sql = new StringBuilder(
                "SELECT af.uuid, af.coach_uuid, u.full_name AS coach_name, af.level_uuid, l.name AS level_name, "
                        + "af.attendance_date, af.pertemuan_ke, af.is_active, af.notes "
                        + "FROM attendance_forms af "
                        + "JOIN users u ON u.uuid = af.coach_uuid "
                        + "JOIN levels l ON l.uuid = af.level_uuid "
                        + "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (coachUuid != null && !coachUuid.trim().isEmpty()) {
            sql.append(" AND af.coach_uuid = ?");
            params.add(coachUuid.trim());
        }
        if (attendanceDate != null) {
            sql.append(" AND af.attendance_date = ?");
            params.add(attendanceDate);
        }
        if (levelUuid != null && !levelUuid.trim().isEmpty()) {
            sql.append(" AND af.level_uuid = ?");
            params.add(levelUuid.trim());
        }
        if (pertemuanKe != null) {
            sql.append(" AND af.pertemuan_ke = ?");
            params.add(pertemuanKe);
        }
        sql.append(" ORDER BY af.attendance_date DESC, af.pertemuan_ke ASC, u.full_name ASC, l.name ASC");

        List<AttendanceForm> forms = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object value = params.get(i);
                if (value instanceof String) {
                    statement.setString(i + 1, (String) value);
                } else if (value instanceof Date) {
                    statement.setDate(i + 1, (Date) value);
                } else if (value instanceof Integer) {
                    statement.setInt(i + 1, (Integer) value);
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    forms.add(mapForm(resultSet));
                }
            }
            return forms;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal filter master absensi.", exception);
        }
    }

    public List<AttendanceForm> findActiveFormsByCoach(String coachUuid) {
        String sql = "SELECT af.uuid, af.coach_uuid, u.full_name AS coach_name, af.level_uuid, l.name AS level_name, "
                + "af.attendance_date, af.pertemuan_ke, af.is_active, af.notes "
                + "FROM attendance_forms af "
                + "JOIN users u ON u.uuid = af.coach_uuid "
                + "JOIN levels l ON l.uuid = af.level_uuid "
                + "WHERE af.is_active = 1 AND af.coach_uuid = ? "
                + "ORDER BY af.attendance_date DESC, af.pertemuan_ke ASC, l.name ASC";
        List<AttendanceForm> forms = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, coachUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    forms.add(mapForm(resultSet));
                }
            }
            return forms;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil form absensi aktif.", exception);
        }
    }

    public AttendanceForm create(AttendanceForm form) {
        String sql = "INSERT INTO attendance_forms "
                + "(uuid, coach_uuid, period_year, period_month, level_uuid, attendance_date, pertemuan_ke, is_active, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String uuid = UUID.randomUUID().toString();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, form.getCoachUuid());
            statement.setInt(3, form.getAttendanceDate().getYear());
            statement.setInt(4, form.getAttendanceDate().getMonthValue());
            statement.setString(5, form.getLevelUuid());
            statement.setDate(6, Date.valueOf(form.getAttendanceDate()));
            statement.setInt(7, form.getPertemuanKe());
            statement.setBoolean(8, form.isActive());
            statement.setString(9, form.getNotes());
            statement.executeUpdate();
            form.setUuid(uuid);
            return form;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan master absensi.", exception);
        }
    }

    public boolean existsMeetingInSameMonth(String coachUuid, String levelUuid, Date attendanceDate, int pertemuanKe, String excludeFormUuid) {
        StringBuilder sql = new StringBuilder(
                "SELECT uuid FROM attendance_forms "
                        + "WHERE coach_uuid = ? AND level_uuid = ? "
                        + "AND YEAR(attendance_date) = YEAR(?) "
                        + "AND MONTH(attendance_date) = MONTH(?) "
                        + "AND pertemuan_ke = ?");
        if (excludeFormUuid != null && !excludeFormUuid.trim().isEmpty()) {
            sql.append(" AND uuid <> ?");
        }
        sql.append(" LIMIT 1");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setString(1, coachUuid);
            statement.setString(2, levelUuid);
            statement.setDate(3, attendanceDate);
            statement.setDate(4, attendanceDate);
            statement.setInt(5, pertemuanKe);
            if (excludeFormUuid != null && !excludeFormUuid.trim().isEmpty()) {
                statement.setString(6, excludeFormUuid);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memvalidasi bentrok pertemuan form absensi.", exception);
        }
    }

    public void update(AttendanceForm form) {
        String sql = "UPDATE attendance_forms SET coach_uuid = ?, period_year = ?, period_month = ?, level_uuid = ?, attendance_date = ?, pertemuan_ke = ?, is_active = ?, notes = ? "
                + "WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, form.getCoachUuid());
            statement.setInt(2, form.getAttendanceDate().getYear());
            statement.setInt(3, form.getAttendanceDate().getMonthValue());
            statement.setString(4, form.getLevelUuid());
            statement.setDate(5, Date.valueOf(form.getAttendanceDate()));
            statement.setInt(6, form.getPertemuanKe());
            statement.setBoolean(7, form.isActive());
            statement.setString(8, form.getNotes());
            statement.setString(9, form.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui master absensi.", exception);
        }
    }

    public void deleteByUuid(String formUuid) {
        String deleteAttendanceRowsSql = "DELETE FROM attendance_records WHERE attendance_form_uuid = ?";
        String clearFormLevelsSql = "DELETE FROM attendance_form_levels WHERE attendance_form_uuid = ?";
        String deleteFormSql = "DELETE FROM attendance_forms WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(deleteAttendanceRowsSql)) {
                    statement.setString(1, formUuid);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(clearFormLevelsSql)) {
                    statement.setString(1, formUuid);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(deleteFormSql)) {
                    statement.setString(1, formUuid);
                    statement.executeUpdate();
                }

                connection.commit();
                connection.setAutoCommit(autoCommit);
            } catch (SQLException exception) {
                connection.rollback();
                connection.setAutoCommit(autoCommit);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus master absensi: " + exception.getMessage(), exception);
        }
    }

    public List<AttendanceChecklistItem> findChecklistByFormUuid(String formUuid) {
        String sql = "SELECT c.full_name AS coach_name, s.full_name AS murid_name, l.name AS level_name, "
                + "ar.tanggal_absensi, ar.pertemuan_ke, ar.status_absensi, ar.catatan "
                + "FROM attendance_records ar "
                + "JOIN users c ON c.uuid = ar.coach_uuid "
                + "JOIN users s ON s.uuid = ar.murid_uuid "
                + "LEFT JOIN levels l ON l.uuid = ar.level_uuid "
                + "WHERE ar.attendance_form_uuid = ? "
                + "ORDER BY ar.tanggal_absensi DESC, ar.pertemuan_ke ASC, s.full_name ASC";
        List<AttendanceChecklistItem> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, formUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AttendanceChecklistItem row = new AttendanceChecklistItem();
                    row.setCoachName(resultSet.getString("coach_name"));
                    row.setMuridName(resultSet.getString("murid_name"));
                    row.setLevelName(resultSet.getString("level_name"));
                    Date date = resultSet.getDate("tanggal_absensi");
                    if (date != null) {
                        row.setAttendanceDate(date.toLocalDate());
                    }
                    row.setPertemuanKe(resultSet.getInt("pertemuan_ke"));
                    row.setStatus(resultSet.getString("status_absensi"));
                    row.setNotes(resultSet.getString("catatan"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil checklist absensi.", exception);
        }
    }

    private AttendanceForm mapForm(ResultSet resultSet) throws SQLException {
        AttendanceForm form = new AttendanceForm();
        form.setUuid(resultSet.getString("uuid"));
        form.setCoachUuid(resultSet.getString("coach_uuid"));
        form.setCoachName(resultSet.getString("coach_name"));
        form.setLevelUuid(resultSet.getString("level_uuid"));
        form.setLevelName(resultSet.getString("level_name"));
        Date attendanceDate = resultSet.getDate("attendance_date");
        if (attendanceDate != null) {
            form.setAttendanceDate(attendanceDate.toLocalDate());
        }
        form.setPertemuanKe(resultSet.getInt("pertemuan_ke"));
        form.setActive(resultSet.getBoolean("is_active"));
        form.setNotes(resultSet.getString("notes"));
        return form;
    }
}
