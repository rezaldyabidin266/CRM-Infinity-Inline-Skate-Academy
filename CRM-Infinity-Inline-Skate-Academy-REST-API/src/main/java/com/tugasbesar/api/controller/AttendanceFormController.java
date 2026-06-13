package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.AttendanceForm;
import com.tugasbesar.app.service.AttendanceFormManagementService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance-forms")
public class AttendanceFormController {
    private final AttendanceFormManagementService service = new AttendanceFormManagementService();

    @GetMapping
    public Object all(
            @RequestParam(value = "coachUuid", required = false) String coachUuid,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "levelUuid", required = false) String levelUuid,
            @RequestParam(value = "pertemuanKe", required = false) Integer pertemuanKe) {
        if (coachUuid != null || date != null || levelUuid != null || pertemuanKe != null) {
            return service.getFormsByFilters(coachUuid, date, levelUuid, pertemuanKe);
        }
        return service.getAllForms();
    }

    @GetMapping("/metadata")
    public Map<String, Object> metadata() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("coaches", service.getCoachUsers());
        response.put("levels", service.getLevels());
        return response;
    }

    @GetMapping("/{formUuid}/checklist")
    public Object checklist(@PathVariable String formUuid) {
        return service.getChecklistByFormUuid(formUuid);
    }

    @PostMapping
    public AttendanceForm create(@RequestBody ApiRequests.AttendanceFormRequest request) {
        return service.createForm(
                request.getCoachUuid(),
                request.getClassLevelUuid(),
                request.getTanggal(),
                request.getPertemuan(),
                request.isStatusFormAktif(),
                request.getCatatan());
    }

    @PutMapping("/{uuid}")
    public Map<String, Object> update(@PathVariable String uuid, @RequestBody ApiRequests.AttendanceFormRequest request) {
        AttendanceForm form = new AttendanceForm();
        form.setUuid(uuid);
        service.updateForm(
                form,
                request.getCoachUuid(),
                request.getClassLevelUuid(),
                request.getTanggal(),
                request.getPertemuan(),
                request.isStatusFormAktif(),
                request.getCatatan());
        return message("Form absensi berhasil diperbarui.");
    }

    @DeleteMapping("/{uuid}")
    public Map<String, Object> delete(@PathVariable String uuid) {
        AttendanceForm form = new AttendanceForm();
        form.setUuid(uuid);
        service.deleteForm(form);
        return message("Form absensi berhasil dihapus.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
