package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController2 {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard2")
    public String dashboard(Authentication authentication, Model model) {
        try {
            Long userId = extractUserId(authentication);
            List<RentalHistoryDto> rentalHistory = bookingService.getCustomerRentalHistory(userId);
            model.addAttribute("rentalHistory", rentalHistory);
        } catch (Exception e) {
            log.error("Error loading customer dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading rental history");
        }
        return "customer/customer-dashboard-anhtq";
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new RuntimeException("User not authenticated");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}

