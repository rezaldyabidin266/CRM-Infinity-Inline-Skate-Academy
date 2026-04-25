package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.Grade;
import com.tugasbesar.app.model.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LevelRepository {
    private static final String LEVEL_SELECT = "SELECT l.uuid, l.name, l.description, l.sort_order, l.grade_uuid, "
            + "g.name AS grade_name, g.grade_value AS grade_value "
            + "FROM levels l "
            + "JOIN grades g ON g.uuid = l.grade_uuid ";

    private final GradeRepository gradeRepository;

    public LevelRepository() {
        this.gradeRepository = new GradeRepository();
    }

    public List<Level> findAllLevels() {
        String sql = LEVEL_SELECT + "ORDER BY g.grade_value ASC, l.sort_order ASC, l.name ASC";
        List<Level> levels = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                levels.add(mapLevel(resultSet));
            }
            return levels;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil daftar level.", exception);
        }
    }

    public Level findByUuid(String uuid) {
        String sql = LEVEL_SELECT + "WHERE l.uuid = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapLevel(resultSet);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data level.", exception);
        }
    }

    public Level findByName(String name) {
        String sql = LEVEL_SELECT + "WHERE LOWER(l.name) = LOWER(?) LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapLevel(resultSet);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data level.", exception);
        }
    }

    public Level findOrCreateByName(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Level wajib diisi.");
        }

        Level existing = findByName(name);
        if (existing != null) {
            return existing;
        }

        Grade defaultGrade = gradeRepository.ensureDefaultGrade();
        String insertSql = "INSERT INTO levels (uuid, name, description, sort_order, grade_uuid) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            String uuid = UUID.randomUUID().toString();
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setString(3, "Level " + name);
            statement.setInt(4, nextSortOrder(connection));
            statement.setString(5, defaultGrade.getUuid());
            statement.executeUpdate();

            Level level = new Level();
            level.setUuid(uuid);
            level.setName(name);
            level.setDescription("Level " + name);
            level.setGradeUuid(defaultGrade.getUuid());
            level.setGradeName(defaultGrade.getName());
            level.setGradeValue(defaultGrade.getGradeValue());
            return level;
        } catch (SQLException exception) {
            Level afterInsert = findByName(name);
            if (afterInsert != null) {
                return afterInsert;
            }
            throw new RuntimeException("Gagal menyimpan level: " + exception.getMessage(), exception);
        }
    }

    public Level create(String name, String description, String gradeUuid) {
        String insertSql = "INSERT INTO levels (uuid, name, description, sort_order, grade_uuid) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            String uuid = UUID.randomUUID().toString();
            statement.setString(1, uuid);
            statement.setString(2, name.trim());
            statement.setString(3, description.trim());
            statement.setInt(4, nextSortOrder(connection));
            statement.setString(5, gradeUuid);
            statement.executeUpdate();
            return findByUuid(uuid);
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal membuat level: " + exception.getMessage(), exception);
        }
    }

    public void update(Level level) {
        String sql = "UPDATE levels SET name = ?, description = ?, grade_uuid = ? WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, level.getName());
            statement.setString(2, level.getDescription());
            statement.setString(3, level.getGradeUuid());
            statement.setString(4, level.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui level.", exception);
        }
    }

    public int countUsersByLevelUuid(String levelUuid) {
        String sql = "SELECT COUNT(*) AS total FROM users WHERE level_uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, levelUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
            return 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memeriksa penggunaan level.", exception);
        }
    }

    public void deleteByUuid(String levelUuid) {
        String sql = "DELETE FROM levels WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, levelUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus level.", exception);
        }
    }

    public boolean gradeExistsByUuid(String gradeUuid) {
        String sql = "SELECT uuid FROM grades WHERE uuid = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gradeUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memvalidasi grade.", exception);
        }
    }

    private Level mapLevel(ResultSet resultSet) throws SQLException {
        Level level = new Level();
        level.setUuid(resultSet.getString("uuid"));
        level.setName(resultSet.getString("name"));
        level.setDescription(resultSet.getString("description"));
        level.setSortOrder(resultSet.getInt("sort_order"));
        level.setGradeUuid(resultSet.getString("grade_uuid"));
        level.setGradeName(resultSet.getString("grade_name"));
        level.setGradeValue(resultSet.getInt("grade_value"));
        return level;
    }

    private int nextSortOrder(Connection connection) throws SQLException {
        String sql = "SELECT COALESCE(MAX(sort_order), 0) + 1 AS next_order FROM levels";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("next_order");
            }
            return 1;
        }
    }
}
