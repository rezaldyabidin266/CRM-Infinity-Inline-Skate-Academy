package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequestMapper;
import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.RoleModulePermission;
import com.tugasbesar.app.service.RoleManagementService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleManagementService service = new RoleManagementService();

    @GetMapping
    public Object all() {
        return service.getAllRoles();
    }

    @GetMapping("/modules")
    public Object modules() {
        return service.getAllModules();
    }

    @GetMapping("/{roleUuid}/permissions")
    public Object permissions(@PathVariable String roleUuid) {
        return service.getModulePermissionsByRole(roleUuid);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody ApiRequests.RoleRequest request) {
        service.createRole(
                request.getKodeRole(),
                request.getNamaRole(),
                request.getDeskripsiRole(),
                ApiRequestMapper.toRolePermissions(request.getHakAksesModules()));
        return message("Role berhasil dibuat.");
    }

    @PutMapping("/{roleUuid}")
    public Map<String, Object> update(@PathVariable String roleUuid, @RequestBody ApiRequests.RoleRequest request) {
        service.updateRole(
                roleUuid,
                request.getNamaRole(),
                request.getDeskripsiRole(),
                ApiRequestMapper.toRolePermissions(request.getHakAksesModules()));
        return message("Role berhasil diperbarui.");
    }

    @PutMapping("/{roleUuid}/modules")
    public Map<String, Object> updateModules(@PathVariable String roleUuid, @RequestBody ApiRequests.RoleRequest request) {
        List<RoleModulePermission> permissions = ApiRequestMapper.toRolePermissions(request.getHakAksesModules());
        service.updateRoleModules(roleUuid, permissions);
        return message("Akses module role berhasil diperbarui.");
    }

    @DeleteMapping("/{roleUuid}")
    public Map<String, Object> delete(@PathVariable String roleUuid) {
        service.deleteRole(roleUuid);
        return message("Role berhasil dihapus.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
