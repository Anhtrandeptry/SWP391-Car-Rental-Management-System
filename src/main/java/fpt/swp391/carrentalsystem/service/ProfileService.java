package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.ChangePasswordRequest;
import fpt.swp391.carrentalsystem.dto.UpdateProfileRequest;
import fpt.swp391.carrentalsystem.dto.UserProfile;

import java.util.Map;

/**
 * Service interface for profile-related operations.
 * Handles user profile retrieval, updates, and password changes.
 */
public interface ProfileService {

    /**
     * Retrieve the user profile for a given user ID.
     *
     * @param userId the ID of the user
     * @return the UserProfile DTO containing user information
     * @throws RuntimeException if user is not found
     */
    UserProfile getProfile(long userId);

    /**
     * Get complete profile view model for display.
     * Encapsulates all logic needed for profile page rendering.
     *
     * @param userId the ID of the user
     * @param passwordChangeSuccess flag indicating password change success
     * @return a Map containing userProfile, initials, stats, and passwordChangeSuccess
     */
    Map<String, Object> getProfileViewModel(long userId, boolean passwordChangeSuccess);

    /**
     * Get edit form view model.
     * Converts current profile to UpdateProfileRequest for form display.
     *
     * @param userId the ID of the user
     * @return a Map containing updateProfileRequest
     */
    Map<String, Object> getEditFormViewModel(long userId);

    /**
     * Update user profile information.
     *
     * @param userId the ID of the user to update
     * @param request the UpdateProfileRequest containing new profile information
     * @throws RuntimeException if user is not found
     */
    void updateProfile(long userId, UpdateProfileRequest request);

    /**
     * Change the password for a user.
     *
     * @param userId the ID of the user
     * @param request the ChangePasswordRequest containing old and new passwords
     * @throws RuntimeException if user is not found, password validation fails, or current password is incorrect
     */
    void changePassword(long userId, ChangePasswordRequest request);

    /**
     * Find a user ID by their login credentials (email or phone).
     *
     * @param login the login identifier (email or phone number)
     * @return the user ID if found
     * @throws RuntimeException if user is not found by the given login
     */
    long findUserIdByLogin(String login);

    /**
     * Get user statistics (trips, favorites, ratings, etc.).
     *
     * @param userId the ID of the user
     * @return a Map containing user statistics
     */
    Map<String, Object> getStats(long userId);
}
