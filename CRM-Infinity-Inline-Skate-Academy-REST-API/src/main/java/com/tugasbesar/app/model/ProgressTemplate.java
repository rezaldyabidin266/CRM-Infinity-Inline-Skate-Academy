package com.tugasbesar.app.model;

public class ProgressTemplate {
    private String uuid;
    private String levelUuid;
    private String levelName;
    private String name;
    private String notes;
    private boolean active;
    private int itemCount;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLevelUuid() {
        return levelUuid;
    }

    public void setLevelUuid(String levelUuid) {
        this.levelUuid = levelUuid;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return name == null ? "-" : name;
    }
}
