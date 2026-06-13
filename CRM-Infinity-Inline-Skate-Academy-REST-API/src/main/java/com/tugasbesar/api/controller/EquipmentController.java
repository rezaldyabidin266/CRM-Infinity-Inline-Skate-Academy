package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.Equipment;
import com.tugasbesar.app.service.EquipmentManagementService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    private final EquipmentManagementService service = new EquipmentManagementService();

    @GetMapping
    public Object all() {
        return service.getAllEquipment();
    }

    @PostMapping
    public Equipment create(@RequestBody ApiRequests.EquipmentRequest request) {
        return service.createEquipment(
                request.getNamaPeralatan(),
                request.getJenisPeralatan(),
                request.getUkuran(),
                request.getJumlah(),
                request.getKondisi(),
                request.getStatus(),
                request.getCatatan());
    }

    @PutMapping("/{uuid}")
    public Map<String, Object> update(@PathVariable String uuid, @RequestBody ApiRequests.EquipmentRequest request) {
        Equipment equipment = new Equipment();
        equipment.setUuid(uuid);
        service.updateEquipment(
                equipment,
                request.getNamaPeralatan(),
                request.getJenisPeralatan(),
                request.getUkuran(),
                request.getJumlah(),
                request.getKondisi(),
                request.getStatus(),
                request.getCatatan());
        return message("Peralatan berhasil diperbarui.");
    }

    @DeleteMapping("/{uuid}")
    public Map<String, Object> delete(@PathVariable String uuid) {
        Equipment equipment = new Equipment();
        equipment.setUuid(uuid);
        service.deleteEquipment(equipment);
        return message("Peralatan berhasil dihapus.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
