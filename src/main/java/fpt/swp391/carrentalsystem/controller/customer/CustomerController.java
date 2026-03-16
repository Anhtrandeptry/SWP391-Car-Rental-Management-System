package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.ProfileService;
import fpt.swp391.carrentalsystem.sercurity.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public CustomerController(ProfileService profileService, UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    private long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new RuntimeException("Không xác định được user đăng nhập.");

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails cud && cud.getId() != null) {
            return cud.getId();
        }

        String login = auth.getName();
        return userRepository.findByEmailOrPhoneNumber(login, login)
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user theo login: " + login));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long userId = currentUserId();
        model.addAttribute("stats", profileService.getStats(userId));
        return "customer/customer-dashboard";
    }
}