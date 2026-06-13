package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Role;
import com.tugasbesar.app.model.RoleModuleAssignment;
import com.tugasbesar.app.model.RoleModulePermission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AccessControlRepository {

    public List<String> findAllRoleCodes() {
        List<String> roleCodes = new ArrayList<>();
        for (Role role : findAllRoles()) {
            roleCodes.add(role.getCode());
        }
        return roleCodes;
    }

    public List<Role> findAllRoles() {
        String sql = "SELECT uuid, code, name, description FROM roles ORDER BY sort_order, code";
        List<Role> roles = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Role role = new Role();
                role.setUuid(resultSet.getString("uuid"));
                role.setCode(resultSet.getString("code"));
                role.setName(resultSet.getString("name"));
                role.setDescription(resultSet.getString("description"));
                roles.add(role);
            }
            return roles;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data role.", exception);
        }
    }

    public boolean roleExists(String roleCode) {
        String sql = "SELECT code FROM roles WHERE code = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memeriksa role.", exception);
        }
    }

    public boolean roleExistsByUuid(String roleUuid) {
        String sql = "SELECT uuid FROM roles WHERE uuid = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleUuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memeriksa role.", exception);
        }
    }

    public Role findRoleByCode(String roleCode) {
        String sql = "SELECT uuid, code, name, description FROM roles WHERE code = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Role role = new Role();
                    role.setUuid(resultSet.getString("uuid"));
                    role.setCode(resultSet.getString("code"));
                    role.setName(resultSet.getString("name"));
                    role.setDescription(resultSet.getString("description"));
                    return role;
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil detail role.", exception);
        }

        throw new IllegalArgumentException("Role tidak tersedia.");
    }

    public Role findRoleByName(String roleName) {
        String sql = "SELECT uuid, code, name, description FROM roles WHERE name = ? LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Role role = new Role();
                    role.setUuid(resultSet.getString("uuid"));
                    role.setCode(resultSet.getString("code"));
                    role.setName(resultSet.getString("name"));
                    role.setDescription(resultSet.getString("description"));
                    return role;
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil detail role.", exception);
        }

        throw new IllegalArgumentException("Role tidak tersedia.");
    }

    public List<AppModule> findModulesByRoleUuid(String roleUuid) {
        String sql = "SELECT m.uuid, m.code, m.name, m.description, "
                + "rm.can_view, rm.can_create, rm.can_update, rm.can_delete, rm.can_export, rm.can_import "
                + "FROM role_modules rm "
                + "JOIN modules m ON m.uuid = rm.module_uuid "
                + "WHERE rm.role_uuid = ? "
                + "AND rm.can_view = 1 "
                + "ORDER BY m.sort_order, m.code";

        List<AppModule> modules = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleUuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AppModule module = new AppModule();
                    module.setUuid(resultSet.getString("uuid"));
                    module.setCode(resultSet.getString("code"));
                    module.setName(resultSet.getString("name"));
                    module.setDescription(resultSet.getString("description"));
                    setModulePermissions(module, resultSet);
                    modules.add(module);
                }
            }
            return modules;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil module berdasarkan role.", exception);
        }
    }

    public List<AppModule> findAllModules() {
        String sql = "SELECT uuid, code, name, description FROM modules ORDER BY sort_order, code";
        List<AppModule> modules = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                AppModule module = new AppModule();
                module.setUuid(resultSet.getString("uuid"));
                module.setCode(resultSet.getString("code"));
                module.setName(resultSet.getString("name"));
                module.setDescription(resultSet.getString("description"));
                module.setCanView(true);
                module.setCanCreate(true);
                module.setCanUpdate(true);
                module.setCanDelete(true);
                module.setCanExport(true);
                module.setCanImport(true);
                modules.add(module);
            }
            return modules;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data module.", exception);
        }
    }

    public Set<String> findModuleCodesByRole(String roleCode) {
        String sql = "SELECT m.code "
                + "FROM role_modules rm "
                + "JOIN roles r ON r.uuid = rm.role_uuid "
                + "JOIN modules m ON m.uuid = rm.module_uuid "
                + "WHERE r.code = ?";
        Set<String> moduleCodes = new HashSet<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    moduleCodes.add(resultSet.getString("code"));
                }
            }
            return moduleCodes;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil akses module role.", exception);
        }
    }

    public Set<String> findModuleCodesByRoleUuid(String roleUuid) {
        String sql = "SELECT m.code "
                + "FROM role_modules rm "
                + "JOIN modules m ON m.uuid = rm.module_uuid "
                + "WHERE rm.role_uuid = ?";
        Set<String> moduleCodes = new HashSet<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleUuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    moduleCodes.add(resultSet.getString("code"));
                }
            }
            return moduleCodes;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil akses module role.", exception);
        }
    }

    public Map<String, RoleModulePermission> findModulePermissionsByRoleUuid(String roleUuid) {
        String sql = "SELECT m.code, rm.can_view, rm.can_create, rm.can_update, rm.can_delete, rm.can_export, rm.can_import "
                + "FROM role_modules rm "
                + "JOIN modules m ON m.uuid = rm.module_uuid "
                + "WHERE rm.role_uuid = ?";
        Map<String, RoleModulePermission> permissions = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleUuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RoleModulePermission permission = mapRoleModulePermission(resultSet);
                    permissions.put(permission.getModuleCode(), permission);
                }
            }
            return permissions;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil permission module role.", exception);
        }
    }

    public void replaceRoleModules(String roleUuid, List<RoleModulePermission> modulePermissions) {
        String deleteSql = "DELETE FROM role_modules WHERE role_uuid = ?";
        String insertSql = "INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import) "
                + "SELECT UUID(), ?, m.uuid, ?, ?, ?, ?, ?, ? FROM modules m WHERE m.code = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
                    statement.setString(1, roleUuid);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    for (RoleModulePermission permission : modulePermissions) {
                        statement.setString(1, roleUuid);
                        statement.setBoolean(2, permission.canView());
                        statement.setBoolean(3, permission.canCreate());
                        statement.setBoolean(4, permission.canUpdate());
                        statement.setBoolean(5, permission.canDelete());
                        statement.setBoolean(6, permission.canExport());
                        statement.setBoolean(7, permission.canImport());
                        statement.setString(8, permission.getModuleCode());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }

                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui akses module role.", exception);
        }
    }

    public void createRole(Role role, List<RoleModulePermission> modulePermissions) {
        String insertRoleSql = "INSERT INTO roles (uuid, code, name, description, sort_order) "
                + "VALUES (?, ?, ?, ?, (SELECT COALESCE(MAX(sort_order), 0) + 1 FROM roles r))";
        String insertRoleModuleSql = "INSERT INTO role_modules (uuid, role_uuid, module_uuid, can_view, can_create, can_update, can_delete, can_export, can_import) "
                + "SELECT ?, ?, m.uuid, ?, ?, ?, ?, ?, ? FROM modules m WHERE m.code = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement(insertRoleSql)) {
                    statement.setString(1, role.getUuid());
                    statement.setString(2, role.getCode());
                    statement.setString(3, role.getName());
                    statement.setString(4, role.getDescription());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(insertRoleModuleSql)) {
                    for (RoleModulePermission permission : modulePermissions) {
                        statement.setString(1, UUID.randomUUID().toString());
                        statement.setString(2, role.getUuid());
                        statement.setBoolean(3, permission.canView());
                        statement.setBoolean(4, permission.canCreate());
                        statement.setBoolean(5, permission.canUpdate());
                        statement.setBoolean(6, permission.canDelete());
                        statement.setBoolean(7, permission.canExport());
                        statement.setBoolean(8, permission.canImport());
                        statement.setString(9, permission.getModuleCode());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal membuat role.", exception);
        }
    }

    public void updateRole(Role role, List<RoleModulePermission> modulePermissions) {
        String updateRoleSql = "UPDATE roles SET name = ?, description = ? WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateRoleSql)) {
            statement.setString(1, role.getName());
            statement.setString(2, role.getDescription());
            statement.setString(3, role.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui role.", exception);
        }

        replaceRoleModules(role.getUuid(), modulePermissions);
    }

    public void deleteRole(String roleUuid) {
        String findRoleSql = "SELECT name FROM roles WHERE uuid = ? LIMIT 1";
        String countUsersSql = "SELECT COUNT(*) AS total FROM users WHERE role_uuid = ?";
        String deleteRoleSql = "DELETE FROM roles WHERE uuid = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                String roleName;
                try (PreparedStatement statement = connection.prepareStatement(findRoleSql)) {
                    statement.setString(1, roleUuid);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new IllegalArgumentException("Role tidak ditemukan.");
                        }
                        roleName = resultSet.getString("name");
                    }
                }

                if ("Murid".equalsIgnoreCase(roleName)) {
                    throw new IllegalArgumentException("Role default Murid tidak boleh dihapus.");
                }

                try (PreparedStatement statement = connection.prepareStatement(countUsersSql)) {
                    statement.setString(1, roleUuid);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next() && resultSet.getInt("total") > 0) {
                            throw new IllegalArgumentException("Role masih dipakai user. Pindahkan user ke role lain dulu sebelum menghapus role ini.");
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(deleteRoleSql)) {
                    statement.setString(1, roleUuid);
                    statement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus role.", exception);
        }
    }

    public List<RoleModuleAssignment> findRoleModuleAssignments() {
        String sql = "SELECT r.code AS role_code, r.name AS role_name, "
                + "m.code AS module_code, m.name AS module_name, m.description AS module_description, "
                + "rm.can_view, rm.can_create, rm.can_update, rm.can_delete, rm.can_export, rm.can_import "
                + "FROM role_modules rm "
                + "JOIN roles r ON r.uuid = rm.role_uuid "
                + "JOIN modules m ON m.uuid = rm.module_uuid "
                + "ORDER BY r.sort_order, r.code, m.sort_order, m.code";

        List<RoleModuleAssignment> assignments = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                RoleModuleAssignment assignment = new RoleModuleAssignment();
                assignment.setRoleCode(resultSet.getString("role_code"));
                assignment.setRoleName(resultSet.getString("role_name"));
                assignment.setModuleCode(resultSet.getString("module_code"));
                assignment.setModuleName(resultSet.getString("module_name"));
                assignment.setModuleDescription(resultSet.getString("module_description"));
                assignment.setCanView(resultSet.getBoolean("can_view"));
                assignment.setCanCreate(resultSet.getBoolean("can_create"));
                assignment.setCanUpdate(resultSet.getBoolean("can_update"));
                assignment.setCanDelete(resultSet.getBoolean("can_delete"));
                assignment.setCanExport(resultSet.getBoolean("can_export"));
                assignment.setCanImport(resultSet.getBoolean("can_import"));
                assignments.add(assignment);
            }
            return assignments;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data role module.", exception);
        }
    }

    private void setModulePermissions(AppModule module, ResultSet resultSet) throws SQLException {
        module.setCanView(resultSet.getBoolean("can_view"));
        module.setCanCreate(resultSet.getBoolean("can_create"));
        module.setCanUpdate(resultSet.getBoolean("can_update"));
        module.setCanDelete(resultSet.getBoolean("can_delete"));
        module.setCanExport(resultSet.getBoolean("can_export"));
        module.setCanImport(resultSet.getBoolean("can_import"));
    }

    private RoleModulePermission mapRoleModulePermission(ResultSet resultSet) throws SQLException {
        RoleModulePermission permission = new RoleModulePermission();
        permission.setModuleCode(resultSet.getString("code"));
        permission.setCanView(resultSet.getBoolean("can_view"));
        permission.setCanCreate(resultSet.getBoolean("can_create"));
        permission.setCanUpdate(resultSet.getBoolean("can_update"));
        permission.setCanDelete(resultSet.getBoolean("can_delete"));
        permission.setCanExport(resultSet.getBoolean("can_export"));
        permission.setCanImport(resultSet.getBoolean("can_import"));
        return permission;
    }
}
