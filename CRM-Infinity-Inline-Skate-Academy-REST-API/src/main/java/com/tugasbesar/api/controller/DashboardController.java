package com.tugasbesar.api.controller;

import com.tugasbesar.api.util.CurrentUserResolver;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.DashboardRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardRepository repository = new DashboardRepository();
    private final CurrentUserResolver currentUserResolver = new CurrentUserResolver();

    @GetMapping
    public Object load(@RequestHeader("X-User-Uuid") String currentUserUuid) {
        User currentUser = currentUserResolver.require(currentUserUuid);
        if (currentUser.isSuperAdmin() || isAdmin(currentUser)) {
            return repository.loadAdminDashboardData();
        }
        if (isCoach(currentUser)) {
            return repository.loadCoachDashboardData(currentUser);
        }
        return repository.loadStudentDashboardData(currentUser);
    }

    private boolean isAdmin(User user) {
        String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
        return role.contains("admin");
    }

    private boolean isCoach(User user) {
        String role = user.getRole() == null ? "" : user.getRole().toLowerCase();
        return role.contains("coach")
                || role.contains("pelatih")
                || role.contains("trainer")
                || role.contains("instruktur");
    }
}
