package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.ChangePasswordRequest;
import fpt.swp391.carrentalsystem.dto.UpdateProfileRequest;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.mapper.ProfileMapper;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Implementation of the ProfileService interface.
 * Handles user profile operations including retrieval, updates, and password changes.
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileMapper profileMapper;

    // ========== 1) GET PROFILE ==========
    @Transactional(readOnly = true)
    @Override
    public UserProfile getProfile(long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));

        return profileMapper.userToUserProfile(u);
    }

    // ========== 2) UPDATE PROFILE ==========
    @Transactional
    @Override
    public void updateProfile(long userId, UpdateProfileRequest f) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found id=" + userId));

        profileMapper.updateProfileRequestToUser(f, u);
        userRepository.save(u);
    }

    // ========== 3) CHANGE PASSWORD ==========
    @Transactional
    @Override
    public void changePassword(long userId, ChangePasswordRequest f) {
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

    // ========== 4) FIND USER ID BY LOGIN ==========
    @Transactional(readOnly = true)
    @Override
    public long findUserIdByLogin(String login) {
        // ưu tiên email
        return userRepository.findByEmail(login)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user theo login: " + login));
    }

    // ========== 5) STATS (tạm thời chưa có booking/feedback) ==========
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> getStats(long userId) {
        return Map.of(
                "trips", 0,
                "favorites", 0,
                "rating", 0.0
        );
    }
}

