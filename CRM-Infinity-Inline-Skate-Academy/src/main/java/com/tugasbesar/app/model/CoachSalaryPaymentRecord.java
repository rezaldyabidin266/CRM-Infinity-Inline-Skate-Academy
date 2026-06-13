package com.tugasbesar.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CoachSalaryPaymentRecord {
    private String uuid;
    private String coachUuid;
    private String coachName;
    private String gradeName;
    private int paymentYear;
    private int paymentMonth;
    private BigDecimal salaryAmount;
    private boolean paid;
    private LocalDateTime paidAt;
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

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
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

    public BigDecimal getSalaryAmount() {
        return salaryAmount;
    }

    public void setSalaryAmount(BigDecimal salaryAmount) {
        this.salaryAmount = salaryAmount;
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
