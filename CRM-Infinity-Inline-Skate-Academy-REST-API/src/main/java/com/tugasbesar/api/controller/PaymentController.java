package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.service.PaymentManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentManagementService service = new PaymentManagementService();

    @GetMapping("/configs")
    public Map<String, Object> configs() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("levels", service.getLevels());
        response.put("levelPaymentConfigs", service.getLevelPaymentConfigs());
        response.put("gradeCoachPaymentRates", service.getGradeCoachPaymentRates());
        response.put("coachPaymentSummaries", service.getCoachPaymentSummaries());
        return response;
    }

    @PostMapping("/configs/level")
    public Map<String, Object> saveLevelConfig(@RequestBody ApiRequests.LevelPaymentConfigRequest request) {
        service.saveLevelPaymentConfig(request.getLevelUuid(), request.getNominalSpp());
        return message("Konfigurasi SPP level berhasil disimpan.");
    }

    @PostMapping("/configs/coach-rate")
    public Map<String, Object> saveCoachRate(@RequestBody ApiRequests.GradeCoachRateRequest request) {
        service.saveGradeCoachPaymentRate(request.getGradeUuid(), request.getNominalPembayaranCoach());
        return message("Tarif coach per grade berhasil disimpan.");
    }

    @GetMapping("/students")
    public Object studentPayments(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "levelUuid", required = false) String levelUuid,
            @RequestParam(value = "statusFilter", required = false) String statusFilter) {
        return service.getStudentPayments(year, month, levelUuid, statusFilter);
    }

    @PatchMapping("/students/{paymentUuid}")
    public Map<String, Object> updateStudentPayment(@PathVariable String paymentUuid, @RequestBody ApiRequests.PaymentStatusRequest request) {
        service.updateStudentPaymentStatus(paymentUuid, request.isStatusPembayaran(), request.getCatatan());
        return message("Status pembayaran murid berhasil diperbarui.");
    }

    @GetMapping("/coach-salaries")
    public Object coachPayments(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "gradeName", required = false) String gradeName,
            @RequestParam(value = "statusFilter", required = false) String statusFilter) {
        return service.getCoachSalaryPayments(year, month, gradeName, statusFilter);
    }

    @PatchMapping("/coach-salaries/{paymentUuid}")
    public Map<String, Object> updateCoachPayment(@PathVariable String paymentUuid, @RequestBody ApiRequests.PaymentStatusRequest request) {
        service.updateCoachSalaryPaymentStatus(paymentUuid, request.isStatusPembayaran(), request.getCatatan());
        return message("Status pembayaran gaji coach berhasil diperbarui.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
