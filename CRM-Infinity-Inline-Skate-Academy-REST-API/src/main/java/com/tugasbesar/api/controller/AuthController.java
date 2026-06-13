package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService = new AuthService();

    @PostMapping("/login")
    public User login(@RequestBody ApiRequests.LoginRequest request) {
        return authService.login(request.getUsernameAtauEmail(), request.getPassword());
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody ApiRequests.RegisterRequest request) {
        authService.register(
                request.getNamaLengkap(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getKonfirmasiPassword(),
                request.getRole() == null || request.getRole().trim().isEmpty() ? "Murid" : request.getRole());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Register berhasil.");
        return response;
    }

    @GetMapping("/roles")
    public List<String> availableRoles() {
        return authService.getAvailableRoles();
    }
}
