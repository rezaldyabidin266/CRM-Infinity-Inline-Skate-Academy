package com.tugasbesar.app.model;

public class ProgressAssessment {
    private String uuid;
    private String muridUuid;
    private String muridName;
    private String coachUuid;
    private String coachName;
    private String templateUuid;
    private String templateName;
    private String assessmentName;
    private String assessmentDate;
    private String notes;
    private String levelName;
    private int totalItems;
    private int passedItems;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getTemplateUuid() {
        return templateUuid;
    }

    public void setTemplateUuid(String templateUuid) {
        this.templateUuid = templateUuid;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getPassedItems() {
        return passedItems;
    }

    public void setPassedItems(int passedItems) {
        this.passedItems = passedItems;
    }
}
