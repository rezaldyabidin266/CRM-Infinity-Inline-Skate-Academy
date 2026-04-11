package com.tugasbesar.app.service;

import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

public class MasterDataService {
    private final UserRepository userRepository;

    public MasterDataService() {
        this.userRepository = new UserRepository();
    }

    public List<User> getMasterMuridUsers() {
        return userRepository.findUsersByRoleKeywords(Arrays.asList("murid", "student", "siswa", "trial"));
    }

    public List<User> getMasterCoachUsers() {
        return userRepository.findUsersByRoleKeywords(Arrays.asList("pelatih", "coach", "trainer", "instruktur"));
    }
}
