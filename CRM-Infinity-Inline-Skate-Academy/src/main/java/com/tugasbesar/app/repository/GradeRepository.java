package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GradeRepository {
    public List<Grade> findAllGrades() {
        String sql = "SELECT uuid, name, description, grade_value, sort_order FROM grades ORDER BY grade_value ASC, sort_order ASC, name ASC";
        List<Grade> grades = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                grades.add(mapGrade(resultSet));
            }
            return grades;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil daftar grade.", exception);
        }
    }

    public Grade findByUuid(String uuid) {
        String sql = "SELECT uuid, name, description, grade_value, sort_order FROM grades WHERE uuid = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapGrade(resultSet);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data grade.", exception);
        }
    }

    public Grade findByName(String name) {
        String sql = "SELECT uuid, name, description, grade_value, sort_order FROM grades WHERE LOWER(name) = LOWER(?) LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapGrade(resultSet);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data grade.", exception);
        }
    }

    public Grade findByGradeValue(int gradeValue) {
        String sql = "SELECT uuid, name, description, grade_value, sort_order FROM grades WHERE grade_value = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gradeValue);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapGrade(resultSet);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data grade.", exception);
        }
    }

    public Grade findDefaultGrade() {
        String sql = "SELECT uuid, name, description, grade_value, sort_order FROM grades ORDER BY grade_value ASC, sort_order ASC LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return mapGrade(resultSet);
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil grade default.", exception);
        }
    }

    public Grade ensureDefaultGrade() {
        Grade existing = findDefaultGrade();
        if (existing != null) {
            return existing;
        }
        return create("Grade 1", "Grade dasar.", 1);
    }

    public Grade create(String name, String description, int gradeValue) {
        String insertSql = "INSERT INTO grades (uuid, name, description, grade_value, sort_order) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            String uuid = UUID.randomUUID().toString();
            statement.setString(1, uuid);
            statement.setString(2, name.trim());
            statement.setString(3, description.trim());
            statement.setInt(4, gradeValue);
            statement.setInt(5, nextSortOrder(connection));
            statement.executeUpdate();

            Grade grade = new Grade();
            grade.setUuid(uuid);
            grade.setName(name.trim());
            grade.setDescription(description.trim());
            grade.setGradeValue(gradeValue);
            return grade;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal membuat grade: " + exception.getMessage(), exception);
        }
    }

    public void update(Grade grade) {
        String sql = "UPDATE grades SET name = ?, description = ?, grade_value = ? WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, grade.getName());
            statement.setString(2, grade.getDescription());
            statement.setInt(3, grade.getGradeValue());
            statement.setString(4, grade.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui grade.", exception);
        }
    }

    public int countLevelsByGradeUuid(String gradeUuid) {
        String sql = "SELECT COUNT(*) AS total FROM levels WHERE grade_uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gradeUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
            return 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memeriksa penggunaan grade.", exception);
        }
    }

    public void deleteByUuid(String gradeUuid) {
        String sql = "DELETE FROM grades WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gradeUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus grade.", exception);
        }
    }

    private Grade mapGrade(ResultSet resultSet) throws SQLException {
        Grade grade = new Grade();
        grade.setUuid(resultSet.getString("uuid"));
        grade.setName(resultSet.getString("name"));
        grade.setDescription(resultSet.getString("description"));
        grade.setGradeValue(resultSet.getInt("grade_value"));
        grade.setSortOrder(resultSet.getInt("sort_order"));
        return grade;
    }

    private int nextSortOrder(Connection connection) throws SQLException {
        String sql = "SELECT COALESCE(MAX(sort_order), 0) + 1 AS next_order FROM grades";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("next_order");
            }
            return 1;
        }
    }
}
