package com.tugasbesar.app.service;

import com.tugasbesar.app.model.Grade;
import com.tugasbesar.app.repository.GradeRepository;

import java.util.List;

public class GradeManagementService {
    private final GradeRepository gradeRepository;

    public GradeManagementService() {
        this.gradeRepository = new GradeRepository();
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAllGrades();
    }

    public Grade createGrade(String name, String description, String gradeValueText) {
        int gradeValue = parseGradeValue(gradeValueText);
        validateInput(name, description, gradeValue, null);
        return gradeRepository.create(name, description, gradeValue);
    }

    public void updateGrade(Grade existingGrade, String name, String description, String gradeValueText) {
        if (existingGrade == null) {
            throw new IllegalArgumentException("Grade tidak ditemukan.");
        }
        int gradeValue = parseGradeValue(gradeValueText);
        validateInput(name, description, gradeValue, existingGrade.getUuid());

        existingGrade.setName(name.trim());
        existingGrade.setDescription(description.trim());
        existingGrade.setGradeValue(gradeValue);
        gradeRepository.update(existingGrade);
    }

    public void deleteGrade(Grade grade) {
        if (grade == null) {
            throw new IllegalArgumentException("Pilih grade yang ingin dihapus.");
        }
        if (grade.getGradeValue() == 1) {
            throw new IllegalArgumentException("Grade dasar tidak boleh dihapus.");
        }

        int usedBy = gradeRepository.countLevelsByGradeUuid(grade.getUuid());
        if (usedBy > 0) {
            throw new IllegalArgumentException("Grade masih dipakai level. Pindahkan level ke grade lain dulu.");
        }
        gradeRepository.deleteByUuid(grade.getUuid());
    }

    private void validateInput(String name, String description, int gradeValue, String currentUuid) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama grade wajib diisi.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi grade wajib diisi.");
        }
        if (gradeValue < 0) {
            throw new IllegalArgumentException("Nilai grade tidak boleh negatif.");
        }

        Grade existingByName = gradeRepository.findByName(name.trim());
        if (existingByName != null && (currentUuid == null || !existingByName.getUuid().equals(currentUuid))) {
            throw new IllegalArgumentException("Nama grade sudah digunakan.");
        }

        Grade existingByValue = gradeRepository.findByGradeValue(gradeValue);
        if (existingByValue != null && (currentUuid == null || !existingByValue.getUuid().equals(currentUuid))) {
            throw new IllegalArgumentException("Nilai grade sudah digunakan.");
        }
    }

    private int parseGradeValue(String gradeValueText) {
        if (gradeValueText == null || gradeValueText.trim().isEmpty()) {
            throw new IllegalArgumentException("Nilai grade wajib diisi.");
        }
        try {
            return Integer.parseInt(gradeValueText.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Nilai grade harus berupa angka.");
        }
    }
}
