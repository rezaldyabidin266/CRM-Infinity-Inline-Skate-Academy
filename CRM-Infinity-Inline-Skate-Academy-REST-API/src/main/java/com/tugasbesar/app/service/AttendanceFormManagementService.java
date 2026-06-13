package com.tugasbesar.app.service;

import com.tugasbesar.app.model.AttendanceChecklistItem;
import com.tugasbesar.app.model.AttendanceForm;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.AttendanceFormRepository;
import com.tugasbesar.app.repository.LevelRepository;
import com.tugasbesar.app.repository.UserRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class AttendanceFormManagementService {
    private final AttendanceFormRepository attendanceFormRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;

    public AttendanceFormManagementService() {
        this.attendanceFormRepository = new AttendanceFormRepository();
        this.userRepository = new UserRepository();
        this.levelRepository = new LevelRepository();
    }

    public List<User> getCoachUsers() {
        return userRepository.findUsersByRoleKeywords(Arrays.asList("pelatih", "coach", "trainer", "instruktur"));
    }

    public List<Level> getLevels() {
        return levelRepository.findAllLevels();
    }

    public List<AttendanceForm> getAllForms() {
        return attendanceFormRepository.findAllForms();
    }

    public List<AttendanceForm> getFormsByFilters(String coachUuid, String dateText, String levelUuid, Integer pertemuanKe) {
        Date date = null;
        if (dateText != null && !dateText.trim().isEmpty()) {
            date = Date.valueOf(parseDate(dateText.trim()));
        }
        return attendanceFormRepository.findByFilters(coachUuid, date, levelUuid, pertemuanKe);
    }

    public AttendanceForm createForm(String coachUuid, String levelUuid, String dateText, String pertemuanText, boolean active, String notes) {
        AttendanceForm form = buildAndValidate(null, coachUuid, levelUuid, dateText, pertemuanText, active, notes);
        return attendanceFormRepository.create(form);
    }

    public void updateForm(AttendanceForm existing, String coachUuid, String levelUuid, String dateText, String pertemuanText, boolean active, String notes) {
        if (existing == null) {
            throw new IllegalArgumentException("Form absensi tidak ditemukan.");
        }
        AttendanceForm updated = buildAndValidate(existing.getUuid(), coachUuid, levelUuid, dateText, pertemuanText, active, notes);
        existing.setCoachUuid(updated.getCoachUuid());
        existing.setLevelUuid(updated.getLevelUuid());
        existing.setAttendanceDate(updated.getAttendanceDate());
        existing.setPertemuanKe(updated.getPertemuanKe());
        existing.setActive(updated.isActive());
        existing.setNotes(updated.getNotes());
        attendanceFormRepository.update(existing);
    }

    public void deleteForm(AttendanceForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Pilih form absensi yang ingin dihapus.");
        }
        attendanceFormRepository.deleteByUuid(form.getUuid());
    }

    public List<AttendanceChecklistItem> getChecklistByFormUuid(String formUuid) {
        if (formUuid == null || formUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Form absensi tidak valid.");
        }
        return attendanceFormRepository.findChecklistByFormUuid(formUuid.trim());
    }

    private AttendanceForm buildAndValidate(String formUuid, String coachUuid, String levelUuid, String dateText, String pertemuanText, boolean active, String notes) {
        if (coachUuid == null || coachUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Coach wajib dipilih.");
        }
        if (levelUuid == null || levelUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Level wajib dipilih.");
        }
        LocalDate date = parseDate(dateText);
        int pertemuan = parsePertemuan(pertemuanText);
        boolean exists = attendanceFormRepository.existsMeetingInSameMonth(
                coachUuid.trim(),
                levelUuid.trim(),
                Date.valueOf(date),
                pertemuan,
                formUuid);
        if (exists) {
            throw new IllegalArgumentException("Di bulan yang sama, pertemuan ini sudah ada untuk coach dan class tersebut.");
        }

        AttendanceForm form = new AttendanceForm();
        form.setUuid(formUuid);
        form.setCoachUuid(coachUuid.trim());
        form.setLevelUuid(levelUuid.trim());
        form.setAttendanceDate(date);
        form.setPertemuanKe(pertemuan);
        form.setActive(active);
        form.setNotes(notes == null ? "" : notes.trim());
        return form;
    }

    private int parsePertemuan(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Pertemuan wajib diisi.");
        }
        try {
            int number = Integer.parseInt(text.trim());
            if (number < 1 || number > 8) {
                throw new IllegalArgumentException("Pertemuan harus 1 sampai 8.");
            }
            return number;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Pertemuan harus angka.");
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal wajib diisi.");
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Format tanggal harus yyyy-MM-dd.");
        }
    }
}
