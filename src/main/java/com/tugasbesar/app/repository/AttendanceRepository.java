package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.AttendanceRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttendanceRepository {
    public List<AttendanceRecord> findByMonth(int year, int month, String coachUuid) {
        StringBuilder sql = new StringBuilder(
                "SELECT ar.uuid, ar.attendance_form_uuid, ar.coach_uuid, ar.murid_uuid, ar.level_uuid, ar.tanggal_absensi, ar.pertemuan_ke, ar.status_absensi, ar.catatan, ar.updated_at, "
                        + "c.full_name AS coach_name, m.full_name AS murid_name "
                        + ", lv.name AS level_name "
                        + "FROM attendance_records ar "
                        + "JOIN users c ON c.uuid = ar.coach_uuid "
                        + "JOIN users m ON m.uuid = ar.murid_uuid "
                        + "LEFT JOIN levels lv ON lv.uuid = ar.level_uuid "
                        + "WHERE YEAR(ar.tanggal_absensi) = ? AND MONTH(ar.tanggal_absensi) = ?");
        if (coachUuid != null && !coachUuid.trim().isEmpty()) {
            sql.append(" AND ar.coach_uuid = ?");
        }
        sql.append(" ORDER BY ar.tanggal_absensi DESC, ar.pertemuan_ke ASC, m.full_name ASC");

        List<AttendanceRecord> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setInt(1, year);
            statement.setInt(2, month);
            if (coachUuid != null && !coachUuid.trim().isEmpty()) {
                statement.setString(3, coachUuid);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapRow(resultSet));
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data absensi.", exception);
        }
    }

    public AttendanceRecord create(AttendanceRecord row) {
        String sql = "INSERT INTO attendance_records "
                + "(uuid, attendance_form_uuid, coach_uuid, murid_uuid, level_uuid, tanggal_absensi, pertemuan_ke, status_absensi, catatan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String uuid = UUID.randomUUID().toString();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, row.getAttendanceFormUuid());
            statement.setString(3, row.getCoachUuid());
            statement.setString(4, row.getMuridUuid());
            statement.setString(5, row.getLevelUuid());
            statement.setDate(6, Date.valueOf(row.getAttendanceDate()));
            statement.setInt(7, row.getPertemuanKe());
            statement.setString(8, row.getStatus());
            statement.setString(9, row.getNotes());
            statement.executeUpdate();
            row.setUuid(uuid);
            return row;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan absensi.", exception);
        }
    }

    public void update(AttendanceRecord row) {
        String sql = "UPDATE attendance_records "
                + "SET attendance_form_uuid = ?, coach_uuid = ?, murid_uuid = ?, level_uuid = ?, tanggal_absensi = ?, pertemuan_ke = ?, status_absensi = ?, catatan = ? "
                + "WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, row.getAttendanceFormUuid());
            statement.setString(2, row.getCoachUuid());
            statement.setString(3, row.getMuridUuid());
            statement.setString(4, row.getLevelUuid());
            statement.setDate(5, Date.valueOf(row.getAttendanceDate()));
            statement.setInt(6, row.getPertemuanKe());
            statement.setString(7, row.getStatus());
            statement.setString(8, row.getNotes());
            statement.setString(9, row.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui absensi.", exception);
        }
    }

    public void deleteByUuid(String uuid) {
        String sql = "DELETE FROM attendance_records WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus absensi.", exception);
        }
    }

    public boolean existsMeetingSlot(String coachUuid, String muridUuid, int year, int month, int pertemuanKe, String excludeUuid) {
        StringBuilder sql = new StringBuilder(
                "SELECT uuid FROM attendance_records "
                        + "WHERE coach_uuid = ? AND murid_uuid = ? AND YEAR(tanggal_absensi) = ? AND MONTH(tanggal_absensi) = ? AND pertemuan_ke = ?");
        if (excludeUuid != null && !excludeUuid.trim().isEmpty()) {
            sql.append(" AND uuid <> ?");
        }
        sql.append(" LIMIT 1");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setString(1, coachUuid);
            statement.setString(2, muridUuid);
            statement.setInt(3, year);
            statement.setInt(4, month);
            statement.setInt(5, pertemuanKe);
            if (excludeUuid != null && !excludeUuid.trim().isEmpty()) {
                statement.setString(6, excludeUuid);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memeriksa slot pertemuan absensi.", exception);
        }
    }

    public AttendanceRecord findByCoachMuridMonthMeeting(String coachUuid, String muridUuid, int year, int month, int pertemuanKe) {
        String sql = "SELECT ar.uuid, ar.attendance_form_uuid, ar.coach_uuid, ar.murid_uuid, ar.level_uuid, ar.tanggal_absensi, ar.pertemuan_ke, ar.status_absensi, ar.catatan, ar.updated_at, "
                + "c.full_name AS coach_name, m.full_name AS murid_name, lv.name AS level_name "
                + "FROM attendance_records ar "
                + "JOIN users c ON c.uuid = ar.coach_uuid "
                + "JOIN users m ON m.uuid = ar.murid_uuid "
                + "LEFT JOIN levels lv ON lv.uuid = ar.level_uuid "
                + "WHERE ar.coach_uuid = ? AND ar.murid_uuid = ? AND YEAR(ar.tanggal_absensi) = ? AND MONTH(ar.tanggal_absensi) = ? AND ar.pertemuan_ke = ? "
                + "LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, coachUuid);
            statement.setString(2, muridUuid);
            statement.setInt(3, year);
            statement.setInt(4, month);
            statement.setInt(5, pertemuanKe);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mencari slot absensi yang sudah ada.", exception);
        }
    }

    public List<AttendanceRecord> findByFormDateMeeting(String formUuid, String coachUuid, String levelUuid, Date attendanceDate, int pertemuanKe) {
        String sql = "SELECT ar.uuid, ar.attendance_form_uuid, ar.coach_uuid, ar.murid_uuid, ar.level_uuid, ar.tanggal_absensi, ar.pertemuan_ke, ar.status_absensi, ar.catatan, ar.updated_at, "
                + "c.full_name AS coach_name, m.full_name AS murid_name, lv.name AS level_name "
                + "FROM attendance_records ar "
                + "JOIN users c ON c.uuid = ar.coach_uuid "
                + "JOIN users m ON m.uuid = ar.murid_uuid "
                + "LEFT JOIN levels lv ON lv.uuid = ar.level_uuid "
                + "WHERE ar.attendance_form_uuid = ? AND ar.coach_uuid = ? AND ar.level_uuid = ? AND ar.tanggal_absensi = ? AND ar.pertemuan_ke = ?";
        List<AttendanceRecord> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, formUuid);
            statement.setString(2, coachUuid);
            statement.setString(3, levelUuid);
            statement.setDate(4, attendanceDate);
            statement.setInt(5, pertemuanKe);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapRow(resultSet));
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil checklist per form absensi.", exception);
        }
    }

    private AttendanceRecord mapRow(ResultSet resultSet) throws SQLException {
        AttendanceRecord row = new AttendanceRecord();
        row.setUuid(resultSet.getString("uuid"));
        row.setAttendanceFormUuid(resultSet.getString("attendance_form_uuid"));
        row.setCoachUuid(resultSet.getString("coach_uuid"));
        row.setCoachName(resultSet.getString("coach_name"));
        row.setMuridUuid(resultSet.getString("murid_uuid"));
        row.setMuridName(resultSet.getString("murid_name"));
        row.setLevelUuid(resultSet.getString("level_uuid"));
        row.setLevelName(resultSet.getString("level_name"));
        Date attendanceDate = resultSet.getDate("tanggal_absensi");
        if (attendanceDate != null) {
            row.setAttendanceDate(attendanceDate.toLocalDate());
        }
        row.setPertemuanKe(resultSet.getInt("pertemuan_ke"));
        row.setStatus(resultSet.getString("status_absensi"));
        row.setNotes(resultSet.getString("catatan"));
        Timestamp updated = resultSet.getTimestamp("updated_at");
        if (updated != null) {
            row.setUpdatedAt(updated.toLocalDateTime());
        }
        return row;
    }
}
