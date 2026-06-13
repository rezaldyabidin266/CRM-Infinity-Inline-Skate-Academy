package com.tugasbesar.app.model;

import java.math.BigDecimal;

public class GradeCoachPaymentRate {
    private String uuid;
    private String gradeUuid;
    private String gradeName;
    private int gradeValue;
    private int coachCount;
    private BigDecimal monthlyRate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGradeUuid() {
        return gradeUuid;
    }

    public void setGradeUuid(String gradeUuid) {
        this.gradeUuid = gradeUuid;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public int getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(int gradeValue) {
        this.gradeValue = gradeValue;
    }

    public int getCoachCount() {
        return coachCount;
    }

    public void setCoachCount(int coachCount) {
        this.coachCount = coachCount;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
    }
}
