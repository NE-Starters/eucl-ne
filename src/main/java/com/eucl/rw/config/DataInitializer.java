package com.eucl.rw.config;

import com.eucl.rw.enums.ERole;
import com.eucl.rw.model.User;
import com.eucl.rw.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@rw.com")) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@rw.com");
            admin.setPhone("123456789");
            admin.setNationalId("123456789");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(ERole.ROLE_ADMIN));
            userRepository.save(admin);
            System.out.println("✅ Admin user created!");
        }
    }
}

