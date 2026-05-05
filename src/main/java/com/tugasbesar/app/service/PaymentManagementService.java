package com.tugasbesar.app.service;

import com.tugasbesar.app.model.CoachPaymentSummary;
import com.tugasbesar.app.model.GradeCoachPaymentRate;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.LevelPaymentConfig;
import com.tugasbesar.app.model.StudentPaymentRecord;
import com.tugasbesar.app.repository.LevelRepository;
import com.tugasbesar.app.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;

public class PaymentManagementService {
    private final PaymentRepository paymentRepository;
    private final LevelRepository levelRepository;

    public PaymentManagementService() {
        this.paymentRepository = new PaymentRepository();
        this.levelRepository = new LevelRepository();
    }

    public List<LevelPaymentConfig> getLevelPaymentConfigs() {
        return paymentRepository.findLevelPaymentConfigs();
    }

    public List<GradeCoachPaymentRate> getGradeCoachPaymentRates() {
        return paymentRepository.findGradeCoachPaymentRates();
    }

    public List<CoachPaymentSummary> getCoachPaymentSummaries() {
        return paymentRepository.findCoachPaymentSummaries();
    }

    public List<Level> getLevels() {
        return levelRepository.findAllLevels();
    }

    public void saveLevelPaymentConfig(String levelUuid, String amountText) {
        if (levelUuid == null || levelUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Level wajib dipilih.");
        }
        BigDecimal amount = parseAmount(amountText, "Nominal SPP level wajib valid.");
        paymentRepository.saveLevelPaymentConfig(levelUuid.trim(), amount);
    }

    public void saveGradeCoachPaymentRate(String gradeUuid, String amountText) {
        if (gradeUuid == null || gradeUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Grade wajib dipilih.");
        }
        BigDecimal amount = parseAmount(amountText, "Nominal pembayaran coach wajib valid.");
        paymentRepository.saveGradeCoachPaymentRate(gradeUuid.trim(), amount);
    }

    public List<StudentPaymentRecord> getStudentPayments(int year, int month, String levelUuid, String statusFilter) {
        paymentRepository.syncStudentPaymentsForPeriod(year, month);
        Boolean paidFilter = null;
        if ("Sudah Bayar".equalsIgnoreCase(statusFilter)) {
            paidFilter = Boolean.TRUE;
        } else if ("Belum Bayar".equalsIgnoreCase(statusFilter)) {
            paidFilter = Boolean.FALSE;
        }
        return paymentRepository.findStudentPayments(year, month, levelUuid, paidFilter);
    }

    public void updateStudentPaymentStatus(String paymentUuid, boolean paid, String notes) {
        if (paymentUuid == null || paymentUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Data pembayaran murid tidak valid.");
        }
        paymentRepository.updateStudentPayment(paymentUuid.trim(), paid, notes);
    }

    private BigDecimal parseAmount(String raw, String message) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        try {
            String normalized = raw.replace("Rp", "").replace(",", "").trim();
            BigDecimal amount = new BigDecimal(normalized);
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Nominal tidak boleh negatif.");
            }
            return amount;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(message);
        }
    }
}
