package com.tugasbesar.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StudentPaymentRecord {
    private String uuid;
    private String muridUuid;
    private String muridName;
    private String username;
    private String gradeName;
    private String levelName;
    private int paymentYear;
    private int paymentMonth;
    private BigDecimal sppAmount;
    private boolean paid;
    private LocalDateTime paidAt;
    private String notes;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getPaymentYear() {
        return paymentYear;
    }

    public void setPaymentYear(int paymentYear) {
        this.paymentYear = paymentYear;
    }

    public int getPaymentMonth() {
        return paymentMonth;
    }

    public void setPaymentMonth(int paymentMonth) {
        this.paymentMonth = paymentMonth;
    }

    public BigDecimal getSppAmount() {
        return sppAmount;
    }

    public void setSppAmount(BigDecimal sppAmount) {
        this.sppAmount = sppAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
