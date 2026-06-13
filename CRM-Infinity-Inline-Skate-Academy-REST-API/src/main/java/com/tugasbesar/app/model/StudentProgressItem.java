package com.tugasbesar.app.model;

public class StudentProgressItem {
    private String recordUuid;
    private String itemUuid;
    private String muridUuid;
    private String muridName;
    private String coachName;
    private String levelName;
    private String templateName;
    private String assessmentUuid;
    private String assessmentName;
    private String assessmentDate;
    private String kodeUnit;
    private String kompetensi;
    private String category;
    private boolean passed;
    private String checkedAt;
    private String notes;

    public String getRecordUuid() {
        return recordUuid;
    }

    public void setRecordUuid(String recordUuid) {
        this.recordUuid = recordUuid;
    }

    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
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

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getAssessmentUuid() {
        return assessmentUuid;
    }

    public void setAssessmentUuid(String assessmentUuid) {
        this.assessmentUuid = assessmentUuid;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public String getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(String assessmentDate) {
        this.assessmentDate = assessmentDate;
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

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(String checkedAt) {
        this.checkedAt = checkedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
