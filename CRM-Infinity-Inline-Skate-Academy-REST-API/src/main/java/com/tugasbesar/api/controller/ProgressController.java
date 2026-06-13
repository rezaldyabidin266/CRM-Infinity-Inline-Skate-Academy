package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequestMapper;
import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.api.util.CurrentUserResolver;
import com.tugasbesar.app.model.ProgressAssessment;
import com.tugasbesar.app.model.ProgressTemplate;
import com.tugasbesar.app.model.ProgressTemplateItem;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.ProgressManagementService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressManagementService service = new ProgressManagementService();
    private final CurrentUserResolver currentUserResolver = new CurrentUserResolver();

    @GetMapping("/metadata")
    public Map<String, Object> metadata(@RequestHeader(value = "X-User-Uuid", required = false) String currentUserUuid) {
        User currentUser = currentUserResolver.require(currentUserUuid);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("levels", service.getLevels());
        response.put("students", service.getCoachStudents(currentUser));
        response.put("templates", service.getTemplates());
        response.put("coachForms", service.getCoachProgressForms(currentUser));
        return response;
    }

    @GetMapping("/templates")
    public Object templates(@RequestParam(value = "levelUuid", required = false) String levelUuid) {
        if (levelUuid != null && !levelUuid.trim().isEmpty()) {
            return service.getActiveTemplatesByLevel(levelUuid);
        }
        return service.getTemplates();
    }

    @PostMapping("/templates")
    public ProgressTemplate createTemplate(@RequestBody ApiRequests.ProgressTemplateRequest request) {
        return service.createTemplate(
                request.getLevelUuid(),
                request.getNamaTemplate(),
                request.getCatatan(),
                request.isStatusAktif());
    }

    @PutMapping("/templates/{uuid}")
    public Map<String, Object> updateTemplate(@PathVariable String uuid, @RequestBody ApiRequests.ProgressTemplateRequest request) {
        ProgressTemplate template = new ProgressTemplate();
        template.setUuid(uuid);
        service.updateTemplate(
                template,
                request.getLevelUuid(),
                request.getNamaTemplate(),
                request.getCatatan(),
                request.isStatusAktif());
        return message("Template progress berhasil diperbarui.");
    }

    @DeleteMapping("/templates/{uuid}")
    public Map<String, Object> deleteTemplate(@PathVariable String uuid) {
        ProgressTemplate template = new ProgressTemplate();
        template.setUuid(uuid);
        service.deleteTemplate(template);
        return message("Template progress berhasil dihapus.");
    }

    @GetMapping("/templates/{templateUuid}/items")
    public Object items(@PathVariable String templateUuid) {
        return service.getItemsByTemplate(templateUuid);
    }

    @PostMapping("/templates/{templateUuid}/items")
    public ProgressTemplateItem createItem(@PathVariable String templateUuid, @RequestBody ApiRequests.ProgressItemRequest request) {
        return service.createItem(
                templateUuid,
                request.getKodeUnit(),
                request.getKompetensi(),
                request.getKategori(),
                request.getUrutan(),
                request.isStatusAktif());
    }

    @PutMapping("/items/{itemUuid}")
    public Map<String, Object> updateItem(@PathVariable String itemUuid, @RequestBody ApiRequests.ProgressItemRequest request) {
        ProgressTemplateItem item = new ProgressTemplateItem();
        item.setUuid(itemUuid);
        item.setTemplateUuid(request.getTemplateUuid());
        service.updateItem(
                item,
                request.getKodeUnit(),
                request.getKompetensi(),
                request.getKategori(),
                request.getUrutan(),
                request.isStatusAktif());
        return message("Item progress berhasil diperbarui.");
    }

    @DeleteMapping("/items/{itemUuid}")
    public Map<String, Object> deleteItem(@PathVariable String itemUuid) {
        ProgressTemplateItem item = new ProgressTemplateItem();
        item.setUuid(itemUuid);
        service.deleteItem(item);
        return message("Item progress berhasil dihapus.");
    }

    @GetMapping("/assessments")
    public Object assessments(
            @RequestParam("muridUuid") String muridUuid,
            @RequestParam(value = "templateUuid", required = false) String templateUuid) {
        return service.getAssessmentsByStudent(muridUuid, templateUuid);
    }

    @PostMapping("/assessments")
    public ProgressAssessment createAssessment(
            @RequestHeader("X-User-Uuid") String currentUserUuid,
            @RequestBody ApiRequests.ProgressAssessmentRequest request) {
        return service.createAssessment(
                currentUserResolver.require(currentUserUuid),
                request.getMuridUuid(),
                request.getTemplateUuid(),
                request.getNamaRiwayatProgress(),
                request.getTanggalProgress(),
                request.getCatatan());
    }

    @GetMapping("/checklist")
    public Object checklist(
            @RequestParam("templateUuid") String templateUuid,
            @RequestParam("muridUuid") String muridUuid,
            @RequestParam("assessmentUuid") String assessmentUuid) {
        return service.getStudentChecklist(templateUuid, muridUuid, assessmentUuid);
    }

    @GetMapping("/overview/{muridUuid}")
    public Object overview(@PathVariable String muridUuid) {
        return service.getStudentProgressOverview(muridUuid);
    }

    @PostMapping("/checklist/save")
    public Map<String, Object> saveChecklist(
            @RequestHeader("X-User-Uuid") String currentUserUuid,
            @RequestBody ApiRequests.ProgressChecklistSaveRequest request) {
        service.saveStudentChecklist(
                currentUserResolver.require(currentUserUuid),
                request.getMuridUuid(),
                request.getTemplateUuid(),
                request.getAssessmentUuid(),
                ApiRequestMapper.toStudentProgressItems(request.getItems()));
        return message("Checklist progress berhasil disimpan.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
