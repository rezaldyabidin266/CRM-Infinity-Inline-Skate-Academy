package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LevelRepository {
    public List<Level> findAllLevels() {
        String sql = "SELECT uuid, name, description, sort_order FROM levels ORDER BY sort_order ASC, name ASC";
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
        String sql = "SELECT uuid, name, description, sort_order FROM levels WHERE uuid = ? LIMIT 1";

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
        String sql = "SELECT uuid, name, description, sort_order FROM levels WHERE LOWER(name) = LOWER(?) LIMIT 1";

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

        String insertSql = "INSERT INTO levels (uuid, name, description, sort_order) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            String uuid = UUID.randomUUID().toString();
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setString(3, "Level " + name);
            statement.setInt(4, nextSortOrder(connection));
            statement.executeUpdate();

            Level level = new Level();
            level.setUuid(uuid);
            level.setName(name);
            level.setDescription("Level " + name);
            return level;
        } catch (SQLException exception) {
            Level afterInsert = findByName(name);
            if (afterInsert != null) {
                return afterInsert;
            }
            throw new RuntimeException("Gagal menyimpan level: " + exception.getMessage(), exception);
        }
    }

    public Level create(String name, String description) {
        String insertSql = "INSERT INTO levels (uuid, name, description, sort_order) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            String uuid = UUID.randomUUID().toString();
            statement.setString(1, uuid);
            statement.setString(2, name.trim());
            statement.setString(3, description.trim());
            statement.setInt(4, nextSortOrder(connection));
            statement.executeUpdate();

            Level level = new Level();
            level.setUuid(uuid);
            level.setName(name.trim());
            level.setDescription(description.trim());
            return level;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal membuat level: " + exception.getMessage(), exception);
        }
    }

    public void update(Level level) {
        String sql = "UPDATE levels SET name = ?, description = ? WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, level.getName());
            statement.setString(2, level.getDescription());
            statement.setString(3, level.getUuid());
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

    private Level mapLevel(ResultSet resultSet) throws SQLException {
        Level level = new Level();
        level.setUuid(resultSet.getString("uuid"));
        level.setName(resultSet.getString("name"));
        level.setDescription(resultSet.getString("description"));
        level.setSortOrder(resultSet.getInt("sort_order"));
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
