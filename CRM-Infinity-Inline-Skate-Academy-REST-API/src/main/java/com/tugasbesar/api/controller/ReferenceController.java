package com.tugasbesar.api.controller;

import com.tugasbesar.app.repository.AccessControlRepository;
import com.tugasbesar.app.service.GradeManagementService;
import com.tugasbesar.app.service.LevelManagementService;
import com.tugasbesar.app.service.MasterDataService;
import com.tugasbesar.app.service.PaymentManagementService;
import com.tugasbesar.app.service.UserManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/references")
public class ReferenceController {
    private final UserManagementService userManagementService = new UserManagementService();
    private final LevelManagementService levelManagementService = new LevelManagementService();
    private final GradeManagementService gradeManagementService = new GradeManagementService();
    private final MasterDataService masterDataService = new MasterDataService();
    private final PaymentManagementService paymentManagementService = new PaymentManagementService();
    private final AccessControlRepository accessControlRepository = new AccessControlRepository();

    @GetMapping
    public Map<String, Object> all() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("roles", userManagementService.getAllRoles());
        response.put("levels", levelManagementService.getAllLevels());
        response.put("grades", gradeManagementService.getAllGrades());
        response.put("modules", accessControlRepository.findAllModules());
        response.put("masterMuridUsers", masterDataService.getMasterMuridUsers());
        response.put("masterCoachUsers", masterDataService.getMasterCoachUsers());
        response.put("paymentLevels", paymentManagementService.getLevels());
        return response;
    }
}
