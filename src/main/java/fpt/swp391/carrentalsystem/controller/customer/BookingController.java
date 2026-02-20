package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.request.CreateBookingRequest;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentInfoDto;
import fpt.swp391.carrentalsystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/customer/booking")
@RequiredArgsConstructor
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public String bookingPage() {
        return "customer/booking";
    }

    @PostMapping("/create")
    public String createBooking(@Valid CreateBookingRequest request,
                               Authentication authentication,
                               Model model) {
        try {
            Long userId = extractUserId(authentication);
            BookingConfirmationDto confirmation = bookingService.createBooking(request, userId);
            model.addAttribute("booking", confirmation);
            return "customer/booking-confirmation";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "customer/booking";
        }
    }

    @GetMapping("/{bookingId}/payment")
    public String paymentPage(@PathVariable Integer bookingId, Model model) {
        try {
            PaymentInfoDto paymentInfo = bookingService.getPaymentInfo(bookingId);
            model.addAttribute("payment", paymentInfo);
            return "customer/booking-payment";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/customer/booking";
        }
    }

    @PostMapping("/{bookingId}/confirm-payment")
    public String confirmPayment(@PathVariable Integer bookingId, Model model) {
        try {
            bookingService.confirmPayment(bookingId);
            model.addAttribute("success", "Thanh toán thành công! Đặt xe được xác nhận.");
            return "customer/booking-success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/customer/booking/" + bookingId + "/payment";
        }
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return Long.valueOf(userDetails.getUsername());
        }
        throw new RuntimeException("Người dùng chưa xác thực");
    }
}
