package com.tugasbesar.api.controller;

import com.tugasbesar.api.dto.ApiRequests;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.UserRepository;
import com.tugasbesar.app.service.UserManagementService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserManagementService service = new UserManagementService();
    private final UserRepository userRepository = new UserRepository();

    @GetMapping
    public Object all() {
        return service.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody ApiRequests.UserRequest request) {
        return service.createUser(
                request.getNamaLengkap(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRoleUuid(),
                request.getLevelUuid(),
                request.getGradeUuid(),
                request.isStatusAktif());
    }

    @PutMapping("/{uuid}")
    public Map<String, Object> update(@PathVariable String uuid, @RequestBody ApiRequests.UserRequest request) {
        User existing = userRepository.findByUuid(uuid);
        service.updateUser(
                existing,
                request.getNamaLengkap(),
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRoleUuid(),
                request.getLevelUuid(),
                request.getGradeUuid(),
                request.isStatusAktif());
        return message("User berhasil diperbarui.");
    }

    @DeleteMapping("/{uuid}")
    public Map<String, Object> delete(
            @PathVariable String uuid,
            @RequestHeader(value = "X-User-Uuid", required = false) String currentUserUuid) {
        User currentUser = currentUserUuid == null || currentUserUuid.trim().isEmpty()
                ? null
                : userRepository.findByUuid(currentUserUuid.trim());
        User target = userRepository.findByUuid(uuid);
        service.deleteUser(target, currentUser);
        return message("User berhasil dihapus.");
    }

    private Map<String, Object> message(String value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", value);
        return response;
    }
}
