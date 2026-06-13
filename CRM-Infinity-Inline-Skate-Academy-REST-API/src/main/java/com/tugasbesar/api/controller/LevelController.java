package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.repository.LevelRepository;
import com.tugasbesar.app.service.LevelManagementService;
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
@RequestMapping("/api/levels")
public class LevelController {
    private final LevelManagementService service = new LevelManagementService();
    private final LevelRepository levelRepository = new LevelRepository();

    @GetMapping
    public Object all() {
        return service.getAllLevels();
    }

    @PostMapping
    public Level create(@RequestBody ApiRequests.LevelRequest request) {
        return service.createLevel(
                request.getNamaLevel(),
                request.getDeskripsiLevel(),
                request.getGradeUuid());
    }

    @PutMapping("/{uuid}")
    public Map<String, Object> update(@PathVariable String uuid, @RequestBody ApiRequests.LevelRequest request) {
        Level level = new Level();
        level.setUuid(uuid);
        service.updateLevel(
                level,
                request.getNamaLevel(),
                request.getDeskripsiLevel(),
                request.getGradeUuid());
        return message("Level berhasil diperbarui.");
    }

    @DeleteMapping("/{uuid}")
    public Map<String, Object> delete(@PathVariable String uuid) {
        Level level = levelRepository.findByUuid(uuid);
        service.deleteLevel(level);
        return message("Level berhasil dihapus.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
