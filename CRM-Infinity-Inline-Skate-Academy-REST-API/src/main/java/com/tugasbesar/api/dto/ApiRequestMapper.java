package com.tugasbesar.api.dto;

import com.tugasbesar.app.model.RoleModulePermission;
import com.tugasbesar.app.model.StudentProgressItem;

import java.util.ArrayList;
import java.util.List;

public final class ApiRequestMapper {
    private ApiRequestMapper() {
    }

    public static List<RoleModulePermission> toRolePermissions(List<ApiRequests.RolePermissionRequest> requests) {
        List<RoleModulePermission> permissions = new ArrayList<RoleModulePermission>();
        if (requests == null) {
            return permissions;
        }
        for (ApiRequests.RolePermissionRequest request : requests) {
            RoleModulePermission permission = new RoleModulePermission();
            permission.setModuleCode(request == null ? null : request.getModuleCode());
            permission.setCanView(request != null && request.isCanView());
            permission.setCanCreate(request != null && request.isCanCreate());
            permission.setCanUpdate(request != null && request.isCanUpdate());
            permission.setCanDelete(request != null && request.isCanDelete());
            permission.setCanExport(request != null && request.isCanExport());
            permission.setCanImport(request != null && request.isCanImport());
            permissions.add(permission);
        }
        return permissions;
    }

    public static List<StudentProgressItem> toStudentProgressItems(List<ApiRequests.ProgressChecklistItemRequest> requests) {
        List<StudentProgressItem> items = new ArrayList<StudentProgressItem>();
        if (requests == null) {
            return items;
        }
        for (ApiRequests.ProgressChecklistItemRequest request : requests) {
            StudentProgressItem item = new StudentProgressItem();
            item.setItemUuid(request == null ? null : request.getItemUuid());
            item.setKodeUnit(request == null ? null : request.getKodeUnit());
            item.setKompetensi(request == null ? null : request.getKompetensi());
            item.setCategory(request == null ? null : request.getKategori());
            item.setPassed(request != null && request.isLolos());
            item.setNotes(request == null ? null : request.getCatatan());
            items.add(item);
        }
        return items;
    }
}
