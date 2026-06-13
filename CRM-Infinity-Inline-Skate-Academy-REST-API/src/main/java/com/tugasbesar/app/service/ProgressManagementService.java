package com.tugasbesar.app.service;

import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.ProgressAssessment;
import com.tugasbesar.app.model.ProgressTemplate;
import com.tugasbesar.app.model.ProgressTemplateItem;
import com.tugasbesar.app.model.StudentProgressItem;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.LevelRepository;
import com.tugasbesar.app.repository.ProgressRepository;
import com.tugasbesar.app.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ProgressManagementService {
    private static final List<String> VALID_ITEM_CATEGORIES = Arrays.asList(
            ProgressTemplateItem.CATEGORY_FISIK,
            ProgressTemplateItem.CATEGORY_TEKNIK,
            ProgressTemplateItem.CATEGORY_FAIRPLAY
    );

    private final ProgressRepository progressRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    public ProgressManagementService() {
        this.progressRepository = new ProgressRepository();
        this.levelRepository = new LevelRepository();
        this.userRepository = new UserRepository();
    }

    public List<Level> getLevels() {
        return levelRepository.findAllLevels();
    }

    public List<User> getCoachStudents(User currentUser) {
        if (isCoach(currentUser)) {
            return userRepository.findMuridUsersForCoachLevelAndGrade(currentUser.getUuid());
        }
        return userRepository.findUsersByRoleKeywords(Arrays.asList("murid", "student", "siswa", "trial"));
    }

    public List<ProgressTemplate> getTemplates() {
        return progressRepository.findAllTemplates();
    }

    public List<ProgressTemplate> getCoachProgressForms(User currentUser) {
        if (isCoach(currentUser)) {
            return progressRepository.findActiveTemplatesByCoachGrade(currentUser.getUuid());
        }
        return progressRepository.findActiveTemplates();
    }

    public List<ProgressTemplate> getActiveTemplatesByLevel(String levelUuid) {
        if (levelUuid == null || levelUuid.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return progressRepository.findActiveTemplatesByLevel(levelUuid.trim());
    }

    public ProgressTemplate createTemplate(String levelUuid, String name, String notes, boolean active) {
        ProgressTemplate template = buildTemplate(null, levelUuid, name, notes, active);
        return progressRepository.createTemplate(template);
    }

    public void updateTemplate(ProgressTemplate existing, String levelUuid, String name, String notes, boolean active) {
        if (existing == null) {
            throw new IllegalArgumentException("Pilih template progress yang ingin diubah.");
        }
        ProgressTemplate updated = buildTemplate(existing.getUuid(), levelUuid, name, notes, active);
        progressRepository.updateTemplate(updated);
    }

    public void deleteTemplate(ProgressTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("Pilih template progress yang ingin dihapus.");
        }
        progressRepository.deleteTemplate(template.getUuid());
    }

    public List<ProgressTemplateItem> getItemsByTemplate(String templateUuid) {
        if (templateUuid == null || templateUuid.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return progressRepository.findItemsByTemplate(templateUuid.trim(), false);
    }

    public ProgressTemplateItem createItem(String templateUuid, String kodeUnit, String kompetensi, String category, String sortOrderText, boolean active) {
        ProgressTemplateItem item = buildItem(null, templateUuid, kodeUnit, kompetensi, category, sortOrderText, active);
        return progressRepository.createItem(item);
    }

    public void updateItem(ProgressTemplateItem existing, String kodeUnit, String kompetensi, String category, String sortOrderText, boolean active) {
        if (existing == null) {
            throw new IllegalArgumentException("Pilih item kompetensi yang ingin diubah.");
        }
        ProgressTemplateItem updated = buildItem(existing.getUuid(), existing.getTemplateUuid(), kodeUnit, kompetensi, category, sortOrderText, active);
        progressRepository.updateItem(updated);
    }

    public void deleteItem(ProgressTemplateItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Pilih item kompetensi yang ingin dihapus.");
        }
        progressRepository.deleteItem(item.getUuid());
    }

    public List<StudentProgressItem> getStudentChecklist(String templateUuid, String muridUuid, String assessmentUuid) {
        if (templateUuid == null || templateUuid.trim().isEmpty()
                || muridUuid == null || muridUuid.trim().isEmpty()
                || assessmentUuid == null || assessmentUuid.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return progressRepository.findStudentChecklist(templateUuid.trim(), muridUuid.trim(), assessmentUuid.trim());
    }

    public List<ProgressAssessment> getAssessmentsByStudent(String muridUuid, String templateUuid) {
        if (muridUuid == null || muridUuid.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return progressRepository.findAssessmentsByStudent(muridUuid.trim(), templateUuid == null ? null : templateUuid.trim());
    }

    public ProgressAssessment createAssessment(User currentUser, String muridUuid, String templateUuid, String assessmentName, String assessmentDate, String notes) {
        if (currentUser == null || currentUser.getUuid() == null) {
            throw new IllegalArgumentException("Session user tidak valid.");
        }
        if (muridUuid == null || muridUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Murid wajib dipilih.");
        }
        if (templateUuid == null || templateUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Template progress wajib dipilih.");
        }
        if (assessmentName == null || assessmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama riwayat progress wajib diisi.");
        }
        if (assessmentDate == null || assessmentDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Tanggal progress wajib diisi.");
        }
        return progressRepository.createAssessment(
                currentUser.getUuid(),
                muridUuid.trim(),
                templateUuid.trim(),
                assessmentName.trim(),
                assessmentDate.trim(),
                notes == null ? "" : notes.trim());
    }

    public ProgressAssessment getOrCreateLevelAssessment(User currentUser, User student, ProgressTemplate template) {
        if (student == null || student.getUuid() == null) {
            throw new IllegalArgumentException("Murid wajib dipilih.");
        }
        if (template == null || template.getUuid() == null) {
            throw new IllegalArgumentException("Form level wajib dipilih.");
        }
        List<ProgressAssessment> existing = getAssessmentsByStudent(student.getUuid(), template.getUuid());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        String levelName = student.getLevelName() == null || student.getLevelName().trim().isEmpty()
                ? "Level"
                : student.getLevelName().trim();
        return createAssessment(
                currentUser,
                student.getUuid(),
                template.getUuid(),
                template.getName() + " - " + levelName,
                LocalDate.now().toString(),
                "");
    }

    public List<StudentProgressItem> getStudentProgressOverview(String muridUuid) {
        if (muridUuid == null || muridUuid.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<ProgressAssessment> assessments = getAssessmentsByStudent(muridUuid.trim(), null);
        List<StudentProgressItem> rows = new java.util.ArrayList<>();
        for (ProgressAssessment assessment : assessments) {
            rows.addAll(progressRepository.findStudentChecklist(
                    assessment.getTemplateUuid(),
                    muridUuid.trim(),
                    assessment.getUuid()));
        }
        return rows;
    }

    public void saveStudentChecklist(User currentUser, String muridUuid, String templateUuid, String assessmentUuid, List<StudentProgressItem> items) {
        if (currentUser == null || currentUser.getUuid() == null) {
            throw new IllegalArgumentException("Session user tidak valid.");
        }
        if (muridUuid == null || muridUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Murid wajib dipilih.");
        }
        if (templateUuid == null || templateUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Template progress wajib dipilih.");
        }
        if (assessmentUuid == null || assessmentUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Riwayat progress wajib dipilih.");
        }
        progressRepository.saveStudentChecklist(currentUser.getUuid(), muridUuid.trim(), templateUuid.trim(), assessmentUuid.trim(), items);
    }

    private ProgressTemplate buildTemplate(String uuid, String levelUuid, String name, String notes, boolean active) {
        if (levelUuid == null || levelUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Level wajib dipilih.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama template wajib diisi.");
        }
        ProgressTemplate template = new ProgressTemplate();
        template.setUuid(uuid);
        template.setLevelUuid(levelUuid.trim());
        template.setName(name.trim());
        template.setNotes(notes == null ? "" : notes.trim());
        template.setActive(active);
        return template;
    }

    private ProgressTemplateItem buildItem(String uuid, String templateUuid, String kodeUnit, String kompetensi, String category, String sortOrderText, boolean active) {
        if (templateUuid == null || templateUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Template wajib dipilih.");
        }
        if (kodeUnit == null || kodeUnit.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode unit wajib diisi.");
        }
        if (kompetensi == null || kompetensi.trim().isEmpty()) {
            throw new IllegalArgumentException("Kompetensi wajib diisi.");
        }
        String normalizedCategory = normalizeCategory(category);
        if (normalizedCategory.isEmpty()) {
            throw new IllegalArgumentException("Kategori item wajib dipilih.");
        }
        int sortOrder = 0;
        if (sortOrderText != null && !sortOrderText.trim().isEmpty()) {
            try {
                sortOrder = Integer.parseInt(sortOrderText.trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Urutan harus angka.");
            }
        }
        ProgressTemplateItem item = new ProgressTemplateItem();
        item.setUuid(uuid);
        item.setTemplateUuid(templateUuid.trim());
        item.setKodeUnit(kodeUnit.trim());
        item.setKompetensi(kompetensi.trim());
        item.setCategory(normalizedCategory);
        item.setSortOrder(sortOrder);
        item.setActive(active);
        return item;
    }

    private String normalizeCategory(String category) {
        String value = category == null ? "" : category.trim().toUpperCase();
        if ("FAIRPLAY".equals(value) || "FAIR PLAY".equals(value)) {
            value = ProgressTemplateItem.CATEGORY_FAIRPLAY;
        }
        if (!VALID_ITEM_CATEGORIES.contains(value)) {
            return "";
        }
        return value;
    }

    private boolean isCoach(User user) {
        String role = user == null || user.getRole() == null ? "" : user.getRole().toLowerCase();
        return role.contains("coach") || role.contains("pelatih") || role.contains("trainer") || role.contains("instruktur");
    }
}
