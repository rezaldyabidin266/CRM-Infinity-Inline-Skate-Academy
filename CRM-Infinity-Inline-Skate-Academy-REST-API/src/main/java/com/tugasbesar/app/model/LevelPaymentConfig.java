package com.tugasbesar.app.model;

import java.math.BigDecimal;

public class LevelPaymentConfig {
    private String uuid;
    private String levelUuid;
    private String levelName;
    private String gradeUuid;
    private String gradeName;
    private BigDecimal monthlySpp;

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

    public BigDecimal getMonthlySpp() {
        return monthlySpp;
    }

    public void setMonthlySpp(BigDecimal monthlySpp) {
        this.monthlySpp = monthlySpp;
    }
}
