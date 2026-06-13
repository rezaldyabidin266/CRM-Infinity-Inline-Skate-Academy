package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.Grade;
import com.tugasbesar.app.repository.GradeRepository;
import com.tugasbesar.app.service.GradeManagementService;
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
@RequestMapping("/api/grades")
public class GradeController {
    private final GradeManagementService service = new GradeManagementService();
    private final GradeRepository gradeRepository = new GradeRepository();

    @GetMapping
    public Object all() {
        return service.getAllGrades();
    }

    @PostMapping
    public Grade create(@RequestBody ApiRequests.GradeRequest request) {
        return service.createGrade(
                request.getNamaGrade(),
                request.getDeskripsiGrade(),
                request.getNilaiGrade());
    }

    @PutMapping("/{uuid}")
    public Map<String, Object> update(@PathVariable String uuid, @RequestBody ApiRequests.GradeRequest request) {
        Grade grade = new Grade();
        grade.setUuid(uuid);
        service.updateGrade(
                grade,
                request.getNamaGrade(),
                request.getDeskripsiGrade(),
                request.getNilaiGrade());
        return message("Grade berhasil diperbarui.");
    }

    @DeleteMapping("/{uuid}")
    public Map<String, Object> delete(@PathVariable String uuid) {
        Grade grade = gradeRepository.findByUuid(uuid);
        service.deleteGrade(grade);
        return message("Grade berhasil dihapus.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
