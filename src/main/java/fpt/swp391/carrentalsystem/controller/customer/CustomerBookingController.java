package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.response.BookingHistoryResponse;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.security.CustomUserDetails; // Đảm bảo import đúng
import fpt.swp391.carrentalsystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerBookingController {

    private final BookingService bookingService;

    @GetMapping("/booking-history")
    public String viewHistory(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return "redirect:/login";
        }

        User currentUser = customUserDetails.getUser();

        List<BookingHistoryResponse> history = bookingService.getCustomerBookingHistory(currentUser);

        model.addAttribute("bookings", history);

        model.addAttribute("totalCount", history.size());
        model.addAttribute("pendingCount", history.stream()
                .filter(b -> b.getStatus().equalsIgnoreCase("Pending")).count());
        model.addAttribute("upcomingCount", history.stream()
                .filter(b -> b.getStatus().equalsIgnoreCase("Accepted")).count());
        model.addAttribute("completedCount", history.stream()
                .filter(b -> b.getStatus().equalsIgnoreCase("Completed")).count());

        BigDecimal totalSpent = history.stream()
                .filter(b -> b.getStatus().equalsIgnoreCase("Completed"))
                .map(BookingHistoryResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalSpent", totalSpent);

        return "customer/booking-history";
    }
}