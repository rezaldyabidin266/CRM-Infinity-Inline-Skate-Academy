package com.tugasbesar.app.service;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Role;
import com.tugasbesar.app.model.RoleModulePermission;
import com.tugasbesar.app.repository.AccessControlRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoleManagementService {
    private final AccessControlRepository accessControlRepository;

    public RoleManagementService() {
        this.accessControlRepository = new AccessControlRepository();
    }

    public List<Role> getAllRoles() {
        return accessControlRepository.findAllRoles();
    }

    public List<AppModule> getAllModules() {
        return accessControlRepository.findAllModules();
    }

    public Set<String> getModuleCodesByRole(String roleUuid) {
        return accessControlRepository.findModuleCodesByRoleUuid(roleUuid);
    }

    public Map<String, RoleModulePermission> getModulePermissionsByRole(String roleUuid) {
        return accessControlRepository.findModulePermissionsByRoleUuid(roleUuid);
    }

    public void updateRoleModules(String roleUuid, List<RoleModulePermission> selectedModulePermissions) {
        if (roleUuid == null || roleUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Role wajib dipilih.");
        }

        if (selectedModulePermissions == null || selectedModulePermissions.isEmpty()) {
            throw new IllegalArgumentException("Minimal pilih satu module untuk role.");
        }

        accessControlRepository.replaceRoleModules(roleUuid, new ArrayList<>(selectedModulePermissions));
    }

    public void createRole(String code, String name, String description, List<RoleModulePermission> selectedModulePermissions) {
        validateRoleInput(code, name, description, selectedModulePermissions, true);

        Role role = new Role();
        role.setUuid(code.trim());
        role.setCode(role.getUuid());
        role.setName(name.trim());
        role.setDescription(description.trim());
        accessControlRepository.createRole(role, new ArrayList<>(selectedModulePermissions));
    }

    public void updateRole(String roleUuid, String name, String description, List<RoleModulePermission> selectedModulePermissions) {
        validateRoleInput(roleUuid, name, description, selectedModulePermissions, false);

        Role role = new Role();
        role.setUuid(roleUuid.trim());
        role.setName(name.trim());
        role.setDescription(description.trim());
        accessControlRepository.updateRole(role, new ArrayList<>(selectedModulePermissions));
    }

    public void deleteRole(String roleUuid) {
        if (roleUuid == null || roleUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Role wajib dipilih.");
        }

        accessControlRepository.deleteRole(roleUuid.trim());
    }

    private void validateRoleInput(String code, String name, String description, List<RoleModulePermission> selectedModulePermissions, boolean creating) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code role wajib diisi.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama role wajib diisi.");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi role wajib diisi.");
        }

        if (selectedModulePermissions == null || selectedModulePermissions.isEmpty()) {
            throw new IllegalArgumentException("Minimal pilih satu module.");
        }

        Set<String> selectedModules = new HashSet<>();
        for (RoleModulePermission permission : selectedModulePermissions) {
            if (permission.getModuleCode() == null || permission.getModuleCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Module tidak valid.");
            }
            if (!permission.canView() && !permission.canCreate() && !permission.canUpdate()
                    && !permission.canDelete() && !permission.canExport() && !permission.canImport()) {
                throw new IllegalArgumentException("Minimal pilih satu permission untuk setiap module yang dipilih.");
            }
            if (!selectedModules.add(permission.getModuleCode())) {
                throw new IllegalArgumentException("Module tidak boleh duplikat.");
            }
        }

        String normalizedCode = code.trim();
        if (creating && accessControlRepository.roleExists(normalizedCode)) {
            throw new IllegalArgumentException("Code role sudah digunakan.");
        }
    }
}
