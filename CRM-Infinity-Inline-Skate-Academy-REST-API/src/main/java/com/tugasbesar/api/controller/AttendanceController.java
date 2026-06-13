package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.AttendanceRecord;
import com.tugasbesar.app.service.AttendanceManagementService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceManagementService service = new AttendanceManagementService();

    @GetMapping("/metadata")
    public Map<String, Object> metadata(@RequestParam(value = "levelUuid", required = false) String levelUuid) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("coaches", service.getCoachUsers());
        response.put("levels", service.getAllLevels());
        response.put("students", levelUuid == null ? java.util.Collections.emptyList() : service.getMuridUsersByLevelUuid(levelUuid));
        return response;
    }

    @GetMapping
    public Object byMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "coachUuid", required = false) String coachUuid) {
        List<AttendanceRecord> records = service.getAttendanceByMonth(year, month, coachUuid);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("records", records);
        response.put("summary", service.calculateSummary(records));
        return response;
    }

    @GetMapping("/forms/{formUuid}")
    public Object formMeeting(
            @PathVariable String formUuid,
            @RequestParam("coachUuid") String coachUuid,
            @RequestParam("levelUuid") String levelUuid,
            @RequestParam("date") String date,
            @RequestParam("pertemuanKe") int pertemuanKe) {
        return service.getFormAttendanceForMeeting(formUuid, coachUuid, levelUuid, date, pertemuanKe);
    }

    @GetMapping("/slot")
    public Object slot(
            @RequestParam("coachUuid") String coachUuid,
            @RequestParam("muridUuid") String muridUuid,
            @RequestParam("date") String date,
            @RequestParam("pertemuanKe") int pertemuanKe) {
        return service.findExistingSlot(coachUuid, muridUuid, date, pertemuanKe);
    }

    @GetMapping("/forms-by-coach/{coachUuid}")
    public Object formsByCoach(@PathVariable String coachUuid) {
        return service.getActiveFormsByCoach(coachUuid);
    }

    @PostMapping
    public AttendanceRecord create(@RequestBody ApiRequests.AttendanceRequest request) {
        return service.createAttendance(
                request.getFormAbsensiUuid(),
                request.getCoachUuid(),
                request.getMuridUuid(),
                request.getClassLevelUuid(),
                request.getTanggalAbsensi(),
                request.getPertemuan(),
                request.getStatusAbsensi(),
                request.getCatatan());
    }

    @PutMapping("/{uuid}")
    public Map<String, Object> update(@PathVariable String uuid, @RequestBody ApiRequests.AttendanceRequest request) {
        AttendanceRecord row = new AttendanceRecord();
        row.setUuid(uuid);
        service.updateAttendance(
                row,
                request.getFormAbsensiUuid(),
                request.getCoachUuid(),
                request.getMuridUuid(),
                request.getClassLevelUuid(),
                request.getTanggalAbsensi(),
                request.getPertemuan(),
                request.getStatusAbsensi(),
                request.getCatatan());
        return message("Data absensi berhasil diperbarui.");
    }

    @DeleteMapping("/{uuid}")
    public Map<String, Object> delete(@PathVariable String uuid) {
        AttendanceRecord row = new AttendanceRecord();
        row.setUuid(uuid);
        service.deleteAttendance(row);
        return message("Data absensi berhasil dihapus.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
