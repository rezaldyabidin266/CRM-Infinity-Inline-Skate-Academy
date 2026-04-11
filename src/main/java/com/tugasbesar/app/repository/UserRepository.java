package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String USER_SELECT = "SELECT u.uuid, u.full_name, u.username, u.email, u.password_hash, "
            + "u.role_uuid, r.name AS role, u.level_uuid, l.name AS level_name, u.is_super_admin, u.is_active, "
            + "u.last_login_at, u.created_at, u.updated_at "
            + "FROM users u "
            + "JOIN roles r ON r.uuid = u.role_uuid "
            + "LEFT JOIN levels l ON l.uuid = u.level_uuid ";

    public boolean existsByUsername(String username) {
        return existsByUsername(username, null);
    }

    public boolean existsByUsername(String username, String excludedUserUuid) {
        String sql = "SELECT uuid FROM users WHERE username = ?";
        if (excludedUserUuid != null) {
            sql += " AND uuid <> ?";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            if (excludedUserUuid != null) {
                statement.setString(2, excludedUserUuid);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memeriksa username.", exception);
        }
    }

    public boolean existsByEmail(String email) {
        return existsByEmail(email, null);
    }

    public boolean existsByEmail(String email, String excludedUserUuid) {
        String sql = "SELECT uuid FROM users WHERE email = ?";
        if (excludedUserUuid != null) {
            sql += " AND uuid <> ?";
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            if (excludedUserUuid != null) {
                statement.setString(2, excludedUserUuid);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memeriksa email.", exception);
        }
    }

    public User save(User user) {
        String sql = "INSERT INTO users (uuid, full_name, username, email, password_hash, role_uuid, level_uuid, is_super_admin, is_active, last_login_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUuid());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPasswordHash());
            statement.setString(6, user.getRoleUuid());
            statement.setString(7, user.getLevelUuid());
            statement.setBoolean(8, user.isSuperAdmin());
            statement.setBoolean(9, user.isActive());

            if (user.getLastLoginAt() == null) {
                statement.setNull(10, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(10, Timestamp.valueOf(user.getLastLoginAt()));
            }

            statement.executeUpdate();
            return user;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan user.", exception);
        }
    }

    public void update(User user) {
        String sql = "UPDATE users SET full_name = ?, username = ?, email = ?, password_hash = ?, "
                + "role_uuid = ?, level_uuid = ?, is_active = ? WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getRoleUuid());
            statement.setString(6, user.getLevelUuid());
            statement.setBoolean(7, user.isActive());
            statement.setString(8, user.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui user.", exception);
        }
    }

    public void deleteByUuid(String userUuid) {
        String sql = "DELETE FROM users WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus user.", exception);
        }
    }

    public User findByUsernameOrEmail(String identity) {
        String sql = USER_SELECT + "WHERE u.username = ? OR u.email = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identity);
            statement.setString(2, identity);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data user.", exception);
        }

        return null;
    }

    public void updateLastLogin(String userUuid) {
        String sql = "UPDATE users SET last_login_at = ? WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(2, userUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui waktu login.", exception);
        }
    }

    public java.util.List<User> findAllUsers() {
        String sql = USER_SELECT + "WHERE u.is_super_admin = 0 ORDER BY u.full_name ASC, u.username ASC";
        java.util.List<User> users = new java.util.ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
            return users;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil daftar user.", exception);
        }
    }

    public User findByUuid(String userUuid) {
        String sql = USER_SELECT + "WHERE u.uuid = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userUuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil detail user.", exception);
        }

        return null;
    }

    public List<User> findUsersByRoleKeywords(List<String> roleKeywords) {
        if (roleKeywords == null || roleKeywords.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder(USER_SELECT)
                .append("WHERE u.is_super_admin = 0 AND (");
        for (int index = 0; index < roleKeywords.size(); index++) {
            if (index > 0) {
                sql.append(" OR ");
            }
            sql.append("LOWER(r.name) LIKE ?");
        }
        sql.append(") ORDER BY u.full_name ASC, u.username ASC");

        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < roleKeywords.size(); index++) {
                statement.setString(index + 1, "%" + roleKeywords.get(index).toLowerCase() + "%");
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapUser(resultSet));
                }
            }
            return users;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data master user.", exception);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUuid(resultSet.getString("uuid"));
        user.setFullName(resultSet.getString("full_name"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));
        user.setRoleUuid(resultSet.getString("role_uuid"));
        user.setLevelUuid(resultSet.getString("level_uuid"));
        user.setLevelName(resultSet.getString("level_name"));
        user.setSuperAdmin(resultSet.getBoolean("is_super_admin"));
        user.setActive(resultSet.getBoolean("is_active"));

        Timestamp lastLogin = resultSet.getTimestamp("last_login_at");
        if (lastLogin != null) {
            user.setLastLoginAt(lastLogin.toLocalDateTime());
        }

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }
}
