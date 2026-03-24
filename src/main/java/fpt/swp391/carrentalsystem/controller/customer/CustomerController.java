package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.BookingService;
import fpt.swp391.carrentalsystem.service.ProfileService;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final BookingService bookingService;

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

        // Load booking history
        List<RentalHistoryDto> bookingHistory = bookingService.getCustomerRentalHistory(userId);
        model.addAttribute("bookingHistory", bookingHistory);
        model.addAttribute("currentTime", LocalDateTime.now());

        return "customer/customer-dashboard";
    }

    @GetMapping("/booking-history")
    public String bookingHistory(Model model) {
        long userId = currentUserId();

        List<RentalHistoryDto> bookingHistory = bookingService.getCustomerRentalHistory(userId);
        model.addAttribute("bookingHistory", bookingHistory);
        model.addAttribute("currentTime", LocalDateTime.now());

        return "customer/booking-history";
    }

    @GetMapping("/booking/{bookingId}")
    public String bookingDetail(@PathVariable Integer bookingId, Model model) {
        try {
            long userId = currentUserId();
            RentalHistoryDto booking = bookingService.getBookingDetailsForCustomer(bookingId, userId);
            model.addAttribute("booking", booking);

            // Add flags for actions
            model.addAttribute("canCancel", bookingService.canCancelBooking(bookingId, userId));
            model.addAttribute("canPay", bookingService.canPayBooking(bookingId));

            return "customer/booking-details";
        } catch (Exception e) {
            log.error("Error loading booking detail: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "redirect:/customer/dashboard";
        }
    }
}