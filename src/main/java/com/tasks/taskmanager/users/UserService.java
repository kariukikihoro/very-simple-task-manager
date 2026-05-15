package com.tasks.taskmanager.users;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public User createEmployee(String username, String password, String fullName) {
        User emp = new User();
        emp.setUsername(username);
        emp.setPassword(encoder.encode(password));
        emp.setFullName(fullName);
        emp.setRole("ROLE_EMPLOYEE");
        return userRepo.save(emp);
    }

    public List<User> findAllEmployees() {
        return userRepo.findAll().stream()
                .filter(u -> "ROLE_EMPLOYEE".equals(u.getRole()))
                .toList();
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public Optional<User> findById(String id) {
        return userRepo.findById(id);
    }
}