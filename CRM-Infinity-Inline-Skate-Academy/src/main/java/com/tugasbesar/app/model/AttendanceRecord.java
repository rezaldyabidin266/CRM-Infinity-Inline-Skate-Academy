package com.tugasbesar.app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRecord {
    private String uuid;
    private String attendanceFormUuid;
    private String coachUuid;
    private String coachName;
    private String muridUuid;
    private String muridName;
    private String levelUuid;
    private String levelName;
    private LocalDate attendanceDate;
    private int pertemuanKe;
    private String status;
    private String notes;
    private LocalDateTime updatedAt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAttendanceFormUuid() {
        return attendanceFormUuid;
    }

    public void setAttendanceFormUuid(String attendanceFormUuid) {
        this.attendanceFormUuid = attendanceFormUuid;
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

    public String getMuridUuid() {
        return muridUuid;
    }

    public void setMuridUuid(String muridUuid) {
        this.muridUuid = muridUuid;
    }

    public String getMuridName() {
        return muridName;
    }

    public void setMuridName(String muridName) {
        this.muridName = muridName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
