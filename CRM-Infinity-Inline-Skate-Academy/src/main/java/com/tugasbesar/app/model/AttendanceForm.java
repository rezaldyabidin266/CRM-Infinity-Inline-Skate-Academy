package com.tugasbesar.app.model;

import java.time.LocalDate;

public class AttendanceForm {
    private String uuid;
    private String coachUuid;
    private String coachName;
    private String levelUuid;
    private String levelName;
    private LocalDate attendanceDate;
    private int pertemuanKe;
    private boolean active;
    private String notes;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCoachUuid() {
        return coachUuid;
    }

    public void setCoachUuid(String coachUuid) {
        this.coachUuid = coachUuid;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
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

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public int getPertemuanKe() {
        return pertemuanKe;
    }

    public void setPertemuanKe(int pertemuanKe) {
        this.pertemuanKe = pertemuanKe;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
