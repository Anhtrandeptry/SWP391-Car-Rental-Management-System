package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.ChangePasswordForm;
import fpt.swp391.carrentalsystem.dto.UpdateProfileForm;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserProfile getProfile(long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));

        UserProfile dto = new UserProfile();
        dto.setUserId(u.getId());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setGender(u.getGender() == null ? null : u.getGender().name());
        dto.setEmail(u.getEmail());
        dto.setPhoneNumber(u.getPhoneNumber());
        dto.setAddress(u.getAddress());
        dto.setNationalId(u.getNationalId());
        dto.setDriversLicense(u.getDriversLicense());
        return dto;
    }

    @Transactional(readOnly = true)
    public boolean isSameEmailOfCurrentUser(long userId, String email) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));
        return u.getEmail() != null && u.getEmail().equalsIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public boolean phoneExistsForOtherUser(long userId, String phone) {
        if (phone == null || phone.isBlank()) return false;
        return userRepository.existsByPhoneNumberAndIdNot(phone, userId);
    }

    @Transactional(readOnly = true)
    public boolean nationalIdExistsForOtherUser(long userId, String nationalId) {
        if (nationalId == null || nationalId.isBlank()) return false;
        return userRepository.existsByNationalIdAndIdNot(nationalId, userId);
    }

    @Transactional(readOnly = true)
    public boolean driversLicenseExistsForOtherUser(long userId, String driversLicense) {
        if (driversLicense == null || driversLicense.isBlank()) return false;
        return userRepository.existsByDriversLicenseAndIdNot(driversLicense, userId);
    }

    @Transactional
    public void updateProfile(long userId, UpdateProfileForm f) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));

        u.setFirstName(f.getFirstName());
        u.setLastName(f.getLastName());

        u.setGender(fpt.swp391.carrentalsystem.enums.Gender.valueOf(f.getGender()));

        u.setPhoneNumber(f.getPhoneNumber());
        u.setAddress(f.getAddress());
        u.setNationalId(f.getNationalId());
        u.setDriversLicense(f.getDriversLicense());

        userRepository.save(u);
    }

    @Transactional
    public void changePassword(long userId, ChangePasswordForm f) {

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));


        if (!passwordEncoder.matches(f.getCurrentPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("CURRENT_PASSWORD_INVALID");
        }


        if (f.getNewPassword().equals(f.getCurrentPassword())) {
            throw new IllegalArgumentException("NEW_EQUALS_CURRENT");
        }

        u.setPasswordHash(passwordEncoder.encode(f.getNewPassword()));
        userRepository.save(u);
    }

    @Transactional(readOnly = true)
    public long findUserIdByLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new RuntimeException("Login trống.");
        }
        return userRepository.findByEmailOrPhoneNumber(login, login)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user theo login: " + login));
    }

    public Map<String, Object> getStats(long userId) {
        return Map.of("trips", 0, "favorites", 0, "rating", 0.0);
    }
}