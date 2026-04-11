package com.tugasbesar.app.service;

import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.model.Role;
import com.tugasbesar.app.repository.AccessControlRepository;
import com.tugasbesar.app.repository.LevelRepository;
import com.tugasbesar.app.repository.UserRepository;
import com.tugasbesar.app.util.PasswordUtil;

import java.util.UUID;
import java.util.List;

public class AuthService {
    private final UserRepository userRepository;
    private final AccessControlRepository accessControlRepository;
    private final LevelRepository levelRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
        this.accessControlRepository = new AccessControlRepository();
        this.levelRepository = new LevelRepository();
    }

    public void register(String fullName, String username, String email, String password, String confirmPassword) {
        register(fullName, username, email, password, confirmPassword, "Murid");
    }

    public void register(String fullName, String username, String email, String password, String confirmPassword, String roleName) {
        validateRegisterInput(fullName, username, email, password, confirmPassword, roleName);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username sudah digunakan.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email sudah digunakan.");
        }

        User user = new User();
        Role role = accessControlRepository.findRoleByName(roleName.trim());
        Level level = levelRepository.findOrCreateByName("Basic");
        user.setUuid(UUID.randomUUID().toString());
        user.setFullName(fullName.trim());
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(role.getName());
        user.setRoleUuid(role.getUuid());
        user.setLevelName(level.getName());
        user.setLevelUuid(level.getUuid());
        user.setSuperAdmin(false);
        user.setActive(true);

        userRepository.save(user);
    }

    public User login(String identity, String password) {
        if (identity == null || identity.trim().isEmpty()) {
            throw new IllegalArgumentException("Username atau email wajib diisi.");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password wajib diisi.");
        }

        User user = userRepository.findByUsernameOrEmail(identity.trim());
        if (user == null) {
            throw new IllegalArgumentException("Akun tidak ditemukan.");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("Akun tidak aktif.");
        }

        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Password salah.");
        }

        if (user.isSuperAdmin()) {
            user.setAccessibleModules(accessControlRepository.findAllModules());
        } else {
            user.setAccessibleModules(accessControlRepository.findModulesByRoleUuid(user.getRoleUuid()));
        }
        userRepository.updateLastLogin(user.getUuid());
        return user;
    }

    public List<String> getAvailableRoles() {
        return accessControlRepository.findAllRoleCodes();
    }

    private void validateRegisterInput(String fullName, String username, String email, String password, String confirmPassword, String roleName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama lengkap wajib diisi.");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username wajib diisi.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email wajib diisi.");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Konfirmasi password tidak cocok.");
        }

        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role wajib dipilih.");
        }
    }
}
