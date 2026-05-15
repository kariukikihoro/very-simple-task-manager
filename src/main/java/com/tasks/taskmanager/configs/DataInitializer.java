package com.tasks.taskmanager.configs;


import com.tasks.taskmanager.users.User;
import com.tasks.taskmanager.users.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder encoder) {

        return args -> {
            if (userRepo.findByUsername("supervisor").isEmpty()) {
                User sup = new User();
                sup.setUsername("supervisor");
                sup.setPassword(encoder.encode("super123"));
                sup.setRole("ROLE_SUPERVISOR");
                sup.setFullName("Mr. Boris");
                userRepo.save(sup);
                System.out.println("Default supervisor created: supervisor / super123");
            }
        };
    }
}
