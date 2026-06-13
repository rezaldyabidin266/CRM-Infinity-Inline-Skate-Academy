package com.tugasbesar.app.service;

import com.tugasbesar.app.model.Equipment;
import com.tugasbesar.app.repository.EquipmentRepository;

import java.util.List;

public class EquipmentManagementService {
    private final EquipmentRepository equipmentRepository;

    public EquipmentManagementService() {
        this.equipmentRepository = new EquipmentRepository();
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public Equipment createEquipment(String name, String type, String size, String quantityText, String condition, String status, String notes) {
        validate(name, type, quantityText, condition, status, null);
        Equipment equipment = buildEquipment(name, type, size, quantityText, condition, status, notes);
        return equipmentRepository.create(equipment);
    }

    public void updateEquipment(Equipment existing, String name, String type, String size, String quantityText, String condition, String status, String notes) {
        if (existing == null) {
            throw new IllegalArgumentException("Peralatan tidak ditemukan.");
        }
        validate(name, type, quantityText, condition, status, existing.getUuid());
        Equipment updated = buildEquipment(name, type, size, quantityText, condition, status, notes);
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setSize(updated.getSize());
        existing.setQuantity(updated.getQuantity());
        existing.setCondition(updated.getCondition());
        existing.setStatus(updated.getStatus());
        existing.setNotes(updated.getNotes());
        equipmentRepository.update(existing);
    }

    public void deleteEquipment(Equipment equipment) {
        if (equipment == null) {
            throw new IllegalArgumentException("Pilih peralatan yang ingin dihapus.");
        }
        equipmentRepository.deleteByUuid(equipment.getUuid());
    }

    private Equipment buildEquipment(String name, String type, String size, String quantityText, String condition, String status, String notes) {
        Equipment equipment = new Equipment();
        equipment.setName(name == null ? "" : name.trim());
        equipment.setType(type == null ? "" : type.trim());
        equipment.setSize(size == null ? "" : size.trim());
        equipment.setQuantity(parseQuantity(quantityText));
        equipment.setCondition(condition == null ? "" : condition.trim());
        equipment.setStatus(status == null ? "" : status.trim());
        equipment.setNotes(notes == null ? "" : notes.trim());
        return equipment;
    }

    private void validate(String name, String type, String quantityText, String condition, String status, String currentUuid) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama peralatan wajib diisi.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Jenis peralatan wajib diisi.");
        }
        int quantity = parseQuantity(quantityText);
        if (quantity < 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh negatif.");
        }
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("Kondisi wajib diisi.");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status wajib diisi.");
        }

        Equipment existingByName = equipmentRepository.findByName(name.trim());
        if (existingByName != null && (currentUuid == null || !existingByName.getUuid().equals(currentUuid))) {
            throw new IllegalArgumentException("Nama peralatan sudah digunakan.");
        }
    }

    private int parseQuantity(String quantityText) {
        if (quantityText == null || quantityText.trim().isEmpty()) {
            throw new IllegalArgumentException("Jumlah wajib diisi.");
        }
        try {
            return Integer.parseInt(quantityText.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Jumlah harus berupa angka.");
        }
    }
}
