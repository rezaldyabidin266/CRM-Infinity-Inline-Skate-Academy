package com.tugasbesar.app.model;

public class ProgressTemplateItem {
    public static final String CATEGORY_FISIK = "FISIK";
    public static final String CATEGORY_TEKNIK = "TEKNIK";
    public static final String CATEGORY_FAIRPLAY = "FAIRPLAY";

    private String uuid;
    private String templateUuid;
    private String kodeUnit;
    private String kompetensi;
    private String category;
    private boolean active;
    private int sortOrder;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTemplateUuid() {
        return templateUuid;
    }

    public void setTemplateUuid(String templateUuid) {
        this.templateUuid = templateUuid;
    }

    public String getKodeUnit() {
        return kodeUnit;
    }

    public void setKodeUnit(String kodeUnit) {
        this.kodeUnit = kodeUnit;
    }

    public String getKompetensi() {
        return kompetensi;
    }

    public void setKompetensi(String kompetensi) {
        this.kompetensi = kompetensi;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
