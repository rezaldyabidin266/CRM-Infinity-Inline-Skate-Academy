package com.tugasbesar.app.service;

import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.repository.LevelRepository;

import java.util.List;

public class LevelManagementService {
    private final LevelRepository levelRepository;

    public LevelManagementService() {
        this.levelRepository = new LevelRepository();
    }

    public List<Level> getAllLevels() {
        return levelRepository.findAllLevels();
    }

    public Level createLevel(String name, String description) {
        validateInput(name, description, null);
        return levelRepository.create(name, description);
    }

    public void updateLevel(Level existingLevel, String name, String description) {
        if (existingLevel == null) {
            throw new IllegalArgumentException("Level tidak ditemukan.");
        }
        validateInput(name, description, existingLevel.getUuid());
        existingLevel.setName(name.trim());
        existingLevel.setDescription(description.trim());
        levelRepository.update(existingLevel);
    }

    public void deleteLevel(Level level) {
        if (level == null) {
            throw new IllegalArgumentException("Pilih level yang ingin dihapus.");
        }
        String normalized = level.getName() == null ? "" : level.getName().trim().toLowerCase();
        if ("basic".equals(normalized) || "intermediate".equals(normalized) || "pro".equals(normalized)) {
            throw new IllegalArgumentException("Level bawaan tidak boleh dihapus.");
        }

        int usedBy = levelRepository.countUsersByLevelUuid(level.getUuid());
        if (usedBy > 0) {
            throw new IllegalArgumentException("Level masih dipakai user. Pindahkan user ke level lain dulu.");
        }
        levelRepository.deleteByUuid(level.getUuid());
    }

    private void validateInput(String name, String description, String currentUuid) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama level wajib diisi.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi level wajib diisi.");
        }

        Level existing = levelRepository.findByName(name.trim());
        if (existing != null && (currentUuid == null || !existing.getUuid().equals(currentUuid))) {
            throw new IllegalArgumentException("Nama level sudah digunakan.");
        }
    }
}
