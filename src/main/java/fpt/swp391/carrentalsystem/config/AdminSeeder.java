package fpt.swp391.carrentalsystem.config;

import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Gender;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@crms.com";

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        User admin = User.builder()
                .firstName("System")
                .lastName("Admin")
                .gender(Gender.OTHER)
                .email(adminEmail)
                .phoneNumber("0909000000")
                .address("HQ")
                .passwordHash(passwordEncoder.encode("Admin@12345"))
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(admin);

        System.out.println("Seeded default admin: " + adminEmail + " / Admin@12345");
    }
}