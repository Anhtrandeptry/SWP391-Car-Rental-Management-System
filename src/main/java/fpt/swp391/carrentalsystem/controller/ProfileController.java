package fpt.swp391.carrentalsystem.controller;

import fpt.swp391.carrentalsystem.dto.ChangePasswordForm;
import fpt.swp391.carrentalsystem.dto.UpdateProfileForm;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import fpt.swp391.carrentalsystem.service.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }


    private int currentUserId() { return 1; }

    private String initials(UserProfile u) {
        StringBuilder sb = new StringBuilder();
        String firstName = u.getFirstName();
        String lastName = u.getLastName();

        if (firstName != null) {
            for (String p : firstName.trim().split("\\s+")) {
                if (!p.isBlank()) sb.append(Character.toUpperCase(p.charAt(0)));
            }
        }
        if (lastName != null && !lastName.isBlank()) {
            sb.append(Character.toUpperCase(lastName.trim().charAt(0)));
        }
        return sb.toString();
    }

    @GetMapping
    public String profile(Model model, @RequestParam(value = "pwd", required = false) String pwd) {
        int userId = currentUserId();
        UserProfile u = service.getProfile(userId);

        model.addAttribute("u", u);
        model.addAttribute("initials", initials(u));
        model.addAttribute("stats", service.getStats(userId));
        model.addAttribute("pwdOk", pwd != null);
        return "profile";
    }

    @GetMapping("/edit")
    public String editForm(Model model) {
        int userId = currentUserId();
        UserProfile u = service.getProfile(userId);

        UpdateProfileForm f = new UpdateProfileForm();
        f.setFirstName(u.getFirstName());
        f.setLastName(u.getLastName());
        f.setGender(u.getGender());
        f.setPhoneNumber(u.getPhoneNumber());
        f.setEmail(u.getEmail());
        f.setAddress(u.getAddress());
        f.setNationalId(u.getNationalId());
        f.setDriversLicense(u.getDriversLicense());

        model.addAttribute("form", f);
        return "profile-edit";
    }

    @PostMapping("/edit")
    public String editSubmit(@ModelAttribute("form") UpdateProfileForm form) {
        service.updateProfile(currentUserId(), form);
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("form", new ChangePasswordForm());
        return "profile-password";
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(@ModelAttribute("form") ChangePasswordForm form, Model model) {
        try {
            service.changePassword(currentUserId(), form);
            return "redirect:/profile?pwd=ok";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "profile-password";
        }
    }
}
