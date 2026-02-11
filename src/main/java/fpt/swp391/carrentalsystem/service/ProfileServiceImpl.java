package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.ChangePasswordRequest;
import fpt.swp391.carrentalsystem.dto.UpdateProfileRequest;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.mapper.ProfileMapper;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ProfileService.
 * Encapsulates all complexity, mapping, and business logic.
 * Controller delegates all work to this service.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileMapper profileMapper;

    /**
     * Retrieve user profile by ID.
     * Uses MapStruct for entity-to-DTO conversion.
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfile getProfile(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return profileMapper.userToUserProfile(user);
    }

    /**
     * Get complete profile view model for display.
     * Encapsulates all logic needed for profile page rendering.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getProfileViewModel(long userId, boolean passwordChangeSuccess) {
        UserProfile userProfile = getProfile(userId);

        Map<String, Object> model = new HashMap<>();
        model.put("userProfile", userProfile);
        model.put("initials", generateInitials(userProfile));
        model.put("stats", getStats(userId));
        model.put("passwordChangeSuccess", passwordChangeSuccess);

        return model;
    }

    /**
     * Get edit form view model.
     * Converts current profile to UpdateProfileRequest for form display.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEditFormViewModel(long userId) {
        UserProfile userProfile = getProfile(userId);

        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setFirstName(userProfile.getFirstName());
        updateRequest.setLastName(userProfile.getLastName());
        updateRequest.setGender(userProfile.getGender());
        updateRequest.setPhoneNumber(userProfile.getPhoneNumber());
        updateRequest.setEmail(userProfile.getEmail());
        updateRequest.setAddress(userProfile.getAddress());
        updateRequest.setNationalId(userProfile.getNationalId());
        updateRequest.setDriversLicense(userProfile.getDriversLicense());

        Map<String, Object> model = new HashMap<>();
        model.put("updateProfileRequest", updateRequest);

        return model;
    }

    /**
     * Update user profile information.
     * Uses MapStruct for DTO-to-entity conversion.
     */
    @Override
    @Transactional
    public void updateProfile(long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        profileMapper.updateProfileRequestToUser(request, user);
        userRepository.save(user);
    }

    /**
     * Change user password with validation.
     * Validates password requirements and matches confirmation.
     */
    @Override
    @Transactional
    public void changePassword(long userId, ChangePasswordRequest request) {
        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters long");
        }

        // Verify password confirmation matches
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password confirmation does not match new password");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Verify current password matches
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Encode and save new password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Find user ID by login (email).
     */
    @Override
    @Transactional(readOnly = true)
    public long findUserIdByLogin(String login) {
        return userRepository.findByEmail(login)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + login));
    }

    /**
     * Get user statistics.
     * Currently returns placeholder values.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStats(long userId) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return Map.of(
                "trips", 0,
                "favorites", 0,
                "rating", 0.0
        );
    }

    /**
     * Generate initials from user's first and last name.
     * Helper method for view model generation.
     */
    private String generateInitials(UserProfile user) {
        StringBuilder initials = new StringBuilder();

        if (user.getFirstName() != null) {
            for (String part : user.getFirstName().trim().split("\\s+")) {
                if (!part.isBlank()) {
                    initials.append(Character.toUpperCase(part.charAt(0)));
                }
            }
        }

        if (user.getLastName() != null && !user.getLastName().isBlank()) {
            initials.append(Character.toUpperCase(user.getLastName().trim().charAt(0)));
        }

        return initials.toString();
    }
}


