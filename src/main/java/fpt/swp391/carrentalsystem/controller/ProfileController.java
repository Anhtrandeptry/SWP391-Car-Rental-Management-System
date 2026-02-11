package fpt.swp391.carrentalsystem.controller;

import fpt.swp391.carrentalsystem.dto.ChangePasswordRequest;
import fpt.swp391.carrentalsystem.dto.UpdateProfileRequest;
import fpt.swp391.carrentalsystem.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Thin controller for profile management.
 * All complexity delegated to ProfileService.
 * Methods are one-liners following the pattern of AuthController.register().
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Display user profile page.
     * GET /profile
     */
    @GetMapping
    public String getProfile(Model model, @RequestParam(value = "pwd", required = false) String pwd) {
        model.addAllAttributes(profileService.getProfileViewModel(1L, pwd != null));
        return "profile";
    }

    /**
     * Display profile edit form.
     * GET /profile/edit
     */
    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        model.addAllAttributes(profileService.getEditFormViewModel(1L));
        return "profile-edit";
    }

    /**
     * Process profile update submission.
     * POST /profile/edit
     */
    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("updateProfileRequest") UpdateProfileRequest request) {
        profileService.updateProfile(1L, request);
        return "redirect:/profile";
    }

    /**
     * Display change password form.
     * GET /profile/change-password
     */
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        return "profile-password";
    }

    /**
     * Process password change submission.
     * POST /profile/change-password
     */
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request) {
        profileService.changePassword(1L, request);
        return "redirect:/profile?pwd=ok";
    }
}
