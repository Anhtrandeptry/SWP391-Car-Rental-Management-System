package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.response.BookingHistoryResponse;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
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
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerBookingController {

    private final BookingService bookingService;

    @GetMapping("/booking-history")
    public String viewOwnerHistory(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User currentOwner = customUserDetails.getUser();
        List<BookingHistoryResponse> history = bookingService.getOwnerBookingHistory(currentOwner);

        model.addAttribute("bookings", history);
        model.addAttribute("totalCount", history.size());
        model.addAttribute("upcomingCount", history.stream().filter(b -> b.getStatus().equals("Accepted")).count());
        model.addAttribute("ongoingCount", history.stream().filter(b -> b.getStatus().equals("Pending")).count());
        model.addAttribute("revenue", history.stream()
                .filter(b -> b.getStatus().equals("Completed"))
                .map(BookingHistoryResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "owner/booking-history";
    }
}