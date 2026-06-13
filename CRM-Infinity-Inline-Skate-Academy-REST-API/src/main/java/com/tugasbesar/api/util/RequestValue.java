package com.tugasbesar.api.util;

import com.tugasbesar.app.model.RoleModulePermission;
import com.tugasbesar.app.model.StudentProgressItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RequestValue {
    private RequestValue() {
    }

    public static String string(Map<String, Object> body, String key) {
        return string(body, key, "");
    }

    public static String string(Map<String, Object> body, String key, String defaultValue) {
        if (body == null || !body.containsKey(key) || body.get(key) == null) {
            return defaultValue;
        }
        return String.valueOf(body.get(key));
    }

    public static boolean bool(Map<String, Object> body, String key, boolean defaultValue) {
        if (body == null || !body.containsKey(key) || body.get(key) == null) {
            return defaultValue;
        }
        Object value = body.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    public static int integer(Map<String, Object> body, String key, int defaultValue) {
        String value = string(body, key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(key + " harus berupa angka.");
        }
    }

    @SuppressWarnings("unchecked")
    public static List<RoleModulePermission> permissions(Object raw) {
        if (raw == null) {
            return new ArrayList<>();
        }
        if (!(raw instanceof List)) {
            throw new IllegalArgumentException("permissions harus berupa array.");
        }
        List<RoleModulePermission> permissions = new ArrayList<>();
        for (Object item : (List<Object>) raw) {
            if (!(item instanceof Map)) {
                throw new IllegalArgumentException("permission item tidak valid.");
            }
            Map<String, Object> row = (Map<String, Object>) item;
            RoleModulePermission permission = new RoleModulePermission();
            permission.setModuleCode(string(row, "moduleCode"));
            permission.setCanView(bool(row, "canView", false));
            permission.setCanCreate(bool(row, "canCreate", false));
            permission.setCanUpdate(bool(row, "canUpdate", false));
            permission.setCanDelete(bool(row, "canDelete", false));
            permission.setCanExport(bool(row, "canExport", false));
            permission.setCanImport(bool(row, "canImport", false));
            permissions.add(permission);
        }
        return permissions;
    }

    @SuppressWarnings("unchecked")
    public static List<StudentProgressItem> studentProgressItems(Object raw) {
        if (raw == null) {
            return new ArrayList<>();
        }
        if (!(raw instanceof List)) {
            throw new IllegalArgumentException("items harus berupa array.");
        }
        List<StudentProgressItem> items = new ArrayList<>();
        for (Object item : (List<Object>) raw) {
            if (!(item instanceof Map)) {
                throw new IllegalArgumentException("item checklist tidak valid.");
            }
            Map<String, Object> row = (Map<String, Object>) item;
            StudentProgressItem progressItem = new StudentProgressItem();
            progressItem.setItemUuid(string(row, "itemUuid", string(row, "templateItemUuid")));
            progressItem.setAssessmentUuid(string(row, "assessmentUuid"));
            progressItem.setMuridUuid(string(row, "muridUuid"));
            progressItem.setKodeUnit(string(row, "kodeUnit"));
            progressItem.setKompetensi(string(row, "kompetensi"));
            progressItem.setCategory(string(row, "category"));
            progressItem.setPassed(bool(row, "passed", bool(row, "checked", false)));
            progressItem.setNotes(string(row, "notes", string(row, "coachNotes")));
            items.add(progressItem);
        }
        return items;
    }
}
