package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final BookingService bookingService;

    @GetMapping("/success/{bookingId}")
    public String paymentSuccess(@PathVariable Integer bookingId, Model model) {
        try {
            model.addAttribute("bookingId", bookingId);
            model.addAttribute("success", "Thanh toán thành công!");
            return "customer/booking-success";
        } catch (Exception e) {
            log.error("Error loading success page: {}", e.getMessage(), e);
            model.addAttribute("error", "Có lỗi xảy ra.");
            return "customer/payment-error";
        }
    }

    @GetMapping("/error")
    public String paymentError(Model model) {
        model.addAttribute("error", "Thanh toán thất bại. Vui lòng thử lại.");
        return "customer/payment-error";
    }
}
