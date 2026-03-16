package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.ChangePasswordForm;
import fpt.swp391.carrentalsystem.dto.UpdateProfileForm;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // GET PROFILE
    public UserProfile getProfile(long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));

        UserProfile dto = new UserProfile();
        dto.setUserId(u.getId());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setGender(u.getGender() == null ? null : u.getGender().name()); // hoặc dto.setGender(u.getGender())
        dto.setEmail(u.getEmail());
        dto.setPhoneNumber(u.getPhoneNumber());
        dto.setAddress(u.getAddress());
        dto.setNationalId(u.getNationalId());
        dto.setDriversLicense(u.getDriversLicense());

        return dto;
    }

    // UPDATE PROFILE
    public void updateProfile(long userId, UpdateProfileForm f) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));

        u.setFirstName(f.getFirstName());
        u.setLastName(f.getLastName());
        if (f.getGender() != null && !f.getGender().isBlank()) {
            try {
                u.setGender(fpt.swp391.carrentalsystem.enums.Gender.valueOf(f.getGender()));
            } catch (Exception ignored) {
            }
        }
        //validate số điện thoại phải 10 kí tự
        if (f.getPhoneNumber() != null && !f.getPhoneNumber().isBlank()) {
            if (!f.getPhoneNumber().matches("\\d{10}")) {
                throw new RuntimeException("Số điện thoại phải gồm đúng 10 chữ số.");
            }
            u.setPhoneNumber(f.getPhoneNumber());
        }
        // validate căn cước 12 số
        if (f.getNationalId() != null && !f.getNationalId().isBlank()) {
            if (!f.getNationalId().matches("\\d{12}")) {
                throw new RuntimeException("Căn cước công dân phải gồm đúng 12 chữ số.");
            }
            u.setNationalId(f.getNationalId());
        }
        // validate blx ô tô 12 số
        if (f.getDriversLicense() != null && !f.getDriversLicense().isBlank()) {
            if (!f.getDriversLicense().matches("\\d{12}")) {
                throw new RuntimeException("Bằng lái xe phải gồm đúng 12 chữ số.");
            }
            u.setDriversLicense(f.getDriversLicense());
        }

        u.setPhoneNumber(f.getPhoneNumber());
        u.setAddress(f.getAddress());
        u.setNationalId(f.getNationalId());
        u.setDriversLicense(f.getDriversLicense());

        userRepository.save(u);
    }

    // CHANGE PASSWORD
    public void changePassword(long userId, ChangePasswordForm f) {
        if (f.getNewPassword() == null || f.getNewPassword().length() < 8) {
            throw new RuntimeException("Mật khẩu mới phải >= 8 ký tự.");
        }
        if (!f.getNewPassword().equals(f.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu mới không khớp.");
        }

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));

        // Login của nhóm dùng BCrypt => phải matches
        if (!passwordEncoder.matches(f.getCurrentPassword(), u.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng.");
        }

        u.setPasswordHash(passwordEncoder.encode(f.getNewPassword()));
        userRepository.save(u);
    }

    // FIND USER ID BY LOGIN
    public long findUserIdByLogin(String login) {
        // ưu tiên email
        return userRepository.findByEmail(login)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user theo login: " + login));
    }

    // ========== 5) STATS (tạm thời chưa có booking/feedback) ==========
    public Map<String, Object> getStats(long userId) {
        return Map.of(
                "trips", 0,
                "favorites", 0,
                "rating", 0.0
        );
    }
}
