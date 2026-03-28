package fpt.swp391.carrentalsystem.controller;

import fpt.swp391.carrentalsystem.dto.ChangePasswordForm;
import fpt.swp391.carrentalsystem.dto.UpdateProfileForm;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import fpt.swp391.carrentalsystem.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    // Trim tất cả String: "   " -> null để NotBlank bắt
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = (auth == null) ? null : auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud && cud.getId() != null) {
            return cud.getId();
        }
        if (auth != null && auth.getName() != null) {
            return service.findUserIdByLogin(auth.getName());
        }
        throw new RuntimeException("Không xác định được user đăng nhập.");
    }

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
    public String profile(Model model,
                          @RequestParam(value = "pwd", required = false) String pwd,
                          @RequestParam(value = "updated", required = false) String updated) {
        long userId = currentUserId();
        UserProfile u = service.getProfile(userId);

        model.addAttribute("u", u);
        model.addAttribute("initials", initials(u));
        model.addAttribute("stats", service.getStats(userId));
        model.addAttribute("pwdOk", pwd != null);
        model.addAttribute("updatedOk", updated != null);
        return "profile";
    }

    @GetMapping("/edit")
    public String editForm(Model model) {
        long userId = currentUserId();
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
    public String editSubmit(@Valid @ModelAttribute("form") UpdateProfileForm form,
                             BindingResult br,
                             Model model) {

        if (br.hasErrors()) {
            return "profile-edit";
        }

        long userId = currentUserId();

        // chặn user sửa email bằng devtools
        if (!service.isSameEmailOfCurrentUser(userId, form.getEmail())) {
            br.rejectValue("email", "email.invalid", "Email không hợp lệ.");
            return "profile-edit";
        }

        // check trùng (hiện lỗi dưới field)
        if (service.phoneExistsForOtherUser(userId, form.getPhoneNumber())) {
            br.rejectValue("phoneNumber", "phone.duplicate", "Số điện thoại đã được sử dụng.");
        }
        if (service.nationalIdExistsForOtherUser(userId, form.getNationalId())) {
            br.rejectValue("nationalId", "nationalId.duplicate", "CMND/CCCD đã được sử dụng.");
        }
        if (service.driversLicenseExistsForOtherUser(userId, form.getDriversLicense())) {
            br.rejectValue("driversLicense", "driversLicense.duplicate", "GPLX đã được sử dụng.");
        }

        if (br.hasErrors()) {
            return "profile-edit";
        }

        try {
            service.updateProfile(userId, form);
            return "redirect:/profile?updated=ok";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "profile-edit";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("form", new ChangePasswordForm());
        return "profile-password";
    }

    @PostMapping("/change-password")
    public String changePasswordSubmit(@Valid @ModelAttribute("form") ChangePasswordForm form,
                                       BindingResult br,
                                       Model model) {
        if (br.hasErrors()) {
            return "profile-password";
        }

        try {
            service.changePassword(currentUserId(), form);
            return "redirect:/profile?pwd=ok";
        } catch (IllegalArgumentException e) {

            // map lỗi về đúng field để show dưới input
            if ("CURRENT_PASSWORD_INVALID".equals(e.getMessage())) {
                br.rejectValue("currentPassword", "current.invalid", "Mật khẩu hiện tại không đúng.");
            } else if ("NEW_EQUALS_CURRENT".equals(e.getMessage())) {
                br.rejectValue("newPassword", "new.same", "Mật khẩu mới không được trùng mật khẩu hiện tại.");
            } else {
                model.addAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
            }

            return "profile-password";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "profile-password";
        }
    }
}