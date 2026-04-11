package com.tugasbesar.app.service;

import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.Role;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.AccessControlRepository;
import com.tugasbesar.app.repository.LevelRepository;
import com.tugasbesar.app.repository.UserRepository;
import com.tugasbesar.app.util.PasswordUtil;

import java.util.List;
import java.util.UUID;

public class UserManagementService {
    private final UserRepository userRepository;
    private final AccessControlRepository accessControlRepository;
    private final LevelRepository levelRepository;

    public UserManagementService() {
        this.userRepository = new UserRepository();
        this.accessControlRepository = new AccessControlRepository();
        this.levelRepository = new LevelRepository();
    }

    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public List<Role> getAllRoles() {
        return accessControlRepository.findAllRoles();
    }

    public List<Level> getAllLevels() {
        return levelRepository.findAllLevels();
    }

    public User createUser(String fullName, String username, String email, String password, String roleUuid, String levelName, boolean active) {
        validateUserInput(null, fullName, username, email, password, roleUuid, levelName);

        User user = new User();
        Role role = findRoleByUuid(roleUuid);
        Level level = levelRepository.findOrCreateByName(levelName.trim());
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
        user.setActive(active);

        return userRepository.save(user);
    }

    public void updateUser(User existingUser, String fullName, String username, String email, String password, String roleUuid, String levelName, boolean active) {
        if (existingUser == null) {
            throw new IllegalArgumentException("User yang dipilih tidak ditemukan.");
        }

        validateUserInput(existingUser.getUuid(), fullName, username, email, password, roleUuid, levelName);

        existingUser.setFullName(fullName.trim());
        existingUser.setUsername(username.trim());
        existingUser.setEmail(email.trim().toLowerCase());
        Role role = findRoleByUuid(roleUuid);
        Level level = levelRepository.findOrCreateByName(levelName.trim());
        existingUser.setRole(role.getName());
        existingUser.setRoleUuid(role.getUuid());
        existingUser.setLevelName(level.getName());
        existingUser.setLevelUuid(level.getUuid());
        existingUser.setActive(active);

        if (password != null && !password.trim().isEmpty()) {
            existingUser.setPasswordHash(PasswordUtil.hash(password));
        }

        userRepository.update(existingUser);
    }

    public void deleteUser(User user, User currentUser) {
        if (user == null) {
            throw new IllegalArgumentException("Pilih user yang ingin dihapus.");
        }

        if (user.isSuperAdmin()) {
            throw new IllegalArgumentException("Akun super admin khusus tidak boleh dihapus.");
        }

        if (currentUser != null && user.getUuid().equals(currentUser.getUuid())) {
            throw new IllegalArgumentException("User yang sedang login tidak boleh menghapus dirinya sendiri.");
        }

        userRepository.deleteByUuid(user.getUuid());
    }

    private void validateUserInput(String currentUserUuid, String fullName, String username, String email, String password, String roleUuid, String levelName) {
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

        if (roleUuid == null || roleUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Role wajib dipilih.");
        }

        if (!accessControlRepository.roleExistsByUuid(roleUuid.trim())) {
            throw new IllegalArgumentException("Role tidak tersedia.");
        }

        if (levelName == null || levelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Level wajib diisi.");
        }

        if (currentUserUuid == null) {
            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("Password minimal 6 karakter.");
            }
        } else if (password != null && !password.trim().isEmpty() && password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }

        if (userRepository.existsByUsername(username.trim(), currentUserUuid)) {
            throw new IllegalArgumentException("Username sudah digunakan.");
        }

        if (userRepository.existsByEmail(email.trim().toLowerCase(), currentUserUuid)) {
            throw new IllegalArgumentException("Email sudah digunakan.");
        }
    }

    private Role findRoleByUuid(String roleUuid) {
        for (Role role : accessControlRepository.findAllRoles()) {
            if (role.getUuid().equals(roleUuid.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role tidak tersedia.");
    }
}
