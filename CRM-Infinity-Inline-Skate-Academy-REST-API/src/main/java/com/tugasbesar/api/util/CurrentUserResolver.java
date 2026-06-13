package com.tugasbesar.api.util;

import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.UserRepository;

public class CurrentUserResolver {
    private final UserRepository userRepository = new UserRepository();

    public User require(String currentUserUuid) {
        if (currentUserUuid == null || currentUserUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("Header X-User-Uuid wajib diisi.");
        }
        User user = userRepository.findByUuid(currentUserUuid.trim());
        if (user == null) {
            throw new IllegalArgumentException("User session tidak ditemukan.");
        }
        return user;
    }
}
