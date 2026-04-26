package com.tugasbesar.app.service;

import com.tugasbesar.app.model.AttendanceForm;
import com.tugasbesar.app.model.AttendanceRecord;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.AttendanceFormRepository;
import com.tugasbesar.app.repository.AttendanceRepository;
import com.tugasbesar.app.repository.LevelRepository;
import com.tugasbesar.app.repository.UserRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AttendanceManagementService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceFormRepository attendanceFormRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;

    public AttendanceManagementService() {
        this.attendanceRepository = new AttendanceRepository();
        this.attendanceFormRepository = new AttendanceFormRepository();
        this.userRepository = new UserRepository();
        this.levelRepository = new LevelRepository();
    }

    public List<User> getCoachUsers() {
        return userRepository.findUsersByRoleKeywords(Arrays.asList("pelatih", "coach", "trainer", "instruktur"));
    }

    public List<User> getMuridUsersByLevelUuid(String levelUuid) {
        return userRepository.findMuridUsersByLevelUuid(levelUuid);
    }

    public List<AttendanceForm> getActiveFormsByCoach(String coachUuid) {
        if (coachUuid == null || coachUuid.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return attendanceFormRepository.findActiveFormsByCoach(coachUuid.trim());
    }

    public List<AttendanceRecord> getAttendanceByMonth(int year, int month, String coachUuid) {
        validateMonthYear(year, month);
        return attendanceRepository.findByMonth(year, month, coachUuid);
    }

    public List<AttendanceRecord> getFormAttendanceForMeeting(String formUuid, String coachUuid, String levelUuid, String dateText, int pertemuanKe) {
        LocalDate date = parseDate(dateText);
        validateMeeting(pertemuanKe);
        return attendanceRepository.findByFormDateMeeting(formUuid, coachUuid, levelUuid, Date.valueOf(date), pertemuanKe);
    }

    public AttendanceRecord findExistingSlot(String coachUuid, String muridUuid, String dateText, int pertemuanKe) {
        LocalDate date = parseDate(dateText);
        validateMeeting(pertemuanKe);
        if (coachUuid == null || coachUuid.trim().isEmpty() || muridUuid == null || muridUuid.trim().isEmpty()) {
            return null;
        }
        return attendanceRepository.findByCoachMuridMonthMeeting(
                coachUuid.trim(),
                muridUuid.trim(),
                date.getYear(),
                date.getMonthValue(),
                pertemuanKe);
    }

    public List<Level> getAllLevels() {
        return levelRepository.findAllLevels();
    }

    public AttendanceRecord createAttendance(String formUuid, String coachUuid, String muridUuid, String levelUuid, String dateText, int pertemuanKe, String status, String notes) {
        validate(formUuid, coachUuid, muridUuid, levelUuid, dateText, pertemuanKe, status, null);
        AttendanceRecord record = buildRecord(formUuid, coachUuid, muridUuid, levelUuid, dateText, pertemuanKe, status, notes);
        return attendanceRepository.create(record);
    }

    public void updateAttendance(AttendanceRecord existing, String formUuid, String coachUuid, String muridUuid, String levelUuid, String dateText, int pertemuanKe, String status, String notes) {
        if (existing == null) {
            throw new IllegalArgumentException("Data absensi tidak ditemukan.");
        }
        validate(formUuid, coachUuid, muridUuid, levelUuid, dateText, pertemuanKe, status, existing.getUuid());
        AttendanceRecord updated = buildRecord(formUuid, coachUuid, muridUuid, levelUuid, dateText, pertemuanKe, status, notes);
        existing.setAttendanceFormUuid(updated.getAttendanceFormUuid());
        existing.setCoachUuid(updated.getCoachUuid());
        existing.setMuridUuid(updated.getMuridUuid());
        existing.setLevelUuid(updated.getLevelUuid());
        existing.setAttendanceDate(updated.getAttendanceDate());
        existing.setPertemuanKe(updated.getPertemuanKe());
        existing.setStatus(updated.getStatus());
        existing.setNotes(updated.getNotes());
        attendanceRepository.update(existing);
    }

    public void deleteAttendance(AttendanceRecord row) {
        if (row == null) {
            throw new IllegalArgumentException("Pilih data absensi yang ingin dihapus.");
        }
        attendanceRepository.deleteByUuid(row.getUuid());
    }

    public Map<String, Integer> calculateSummary(List<AttendanceRecord> records) {
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("Total", 0);
        summary.put("Hadir", 0);
        summary.put("Izin", 0);
        summary.put("Sakit", 0);
        summary.put("Alpha", 0);

        for (AttendanceRecord record : records) {
            summary.put("Total", summary.get("Total") + 1);
            String key = normalizeStatus(record.getStatus());
            summary.put(key, summary.getOrDefault(key, 0) + 1);
        }
        return summary;
    }

    private AttendanceRecord buildRecord(String formUuid, String coachUuid, String muridUuid, String levelUuid, String dateText, int pertemuanKe, String status, String notes) {
        AttendanceRecord record = new AttendanceRecord();
        record.setAttendanceFormUuid(formUuid == null ? "" : formUuid.trim());
        record.setCoachUuid(coachUuid == null ? "" : coachUuid.trim());
        record.setMuridUuid(muridUuid == null ? "" : muridUuid.trim());
        record.setLevelUuid(levelUuid == null ? "" : levelUuid.trim());
        record.setAttendanceDate(parseDate(dateText));
        record.setPertemuanKe(pertemuanKe);
        record.setStatus(normalizeStatus(status));
        record.setNotes(notes == null ? "" : notes.trim());
        return record;
    }

    private void validate(String formUuid, String coachUuid, String muridUuid, String levelUuid, String dateText, int pertemuanKe, String status, String currentUuid) {
        if (formUuid == null || formUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Form absensi wajib dipilih.");
        }
        if (coachUuid == null || coachUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Coach wajib dipilih.");
        }
        if (muridUuid == null || muridUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Murid wajib dipilih.");
        }
        if (levelUuid == null || levelUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Level absensi wajib dipilih.");
        }
        LocalDate date = parseDate(dateText);
        validateMeeting(pertemuanKe);
        String normalizedStatus = normalizeStatus(status);
        if (normalizedStatus.isEmpty()) {
            throw new IllegalArgumentException("Status absensi wajib dipilih.");
        }

        boolean used = attendanceRepository.existsMeetingSlot(
                coachUuid.trim(),
                muridUuid.trim(),
                date.getYear(),
                date.getMonthValue(),
                pertemuanKe,
                currentUuid);
        if (used) {
            throw new IllegalArgumentException("Slot pertemuan ini sudah terisi untuk coach dan murid tersebut di bulan yang sama.");
        }
    }

    private void validateMonthYear(int year, int month) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Tahun filter tidak valid.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Bulan filter tidak valid.");
        }
    }

    private void validateMeeting(int pertemuanKe) {
        if (pertemuanKe < 1 || pertemuanKe > 8) {
            throw new IllegalArgumentException("Pertemuan hanya boleh 1 sampai 8 per bulan.");
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal absensi wajib diisi.");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Format tanggal harus yyyy-MM-dd.");
        }
    }

    private String normalizeStatus(String status) {
        String value = status == null ? "" : status.trim();
        if (value.isEmpty()) {
            return "";
        }
        if ("hadir".equalsIgnoreCase(value)) {
            return "Hadir";
        }
        if ("izin".equalsIgnoreCase(value)) {
            return "Izin";
        }
        if ("sakit".equalsIgnoreCase(value)) {
            return "Sakit";
        }
        if ("alpha".equalsIgnoreCase(value) || "alfa".equalsIgnoreCase(value)) {
            return "Alpha";
        }
        return value;
    }
}
