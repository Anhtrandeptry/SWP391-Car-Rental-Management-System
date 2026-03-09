package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.request.CreateBookingRequest;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentInfoDto;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/customer/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @GetMapping
    public String bookingPage() {
        return "customer/booking";
    }

    @GetMapping("/api/cars")
    @ResponseBody
    public List<CarResponseDto> getAvailableCars() {
        return bookingService.getAvailableCars();
    }

    @PostMapping("/create")
    public String createBooking(@Valid CreateBookingRequest request,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model) {
        // Check validation errors
        if (bindingResult.hasErrors()) {
            log.warn("Booking validation failed: {}", bindingResult.getAllErrors());
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "customer/booking";
        }

        try {
            Long userId = extractUserId(authentication);
            BookingConfirmationDto confirmation = bookingService.createBooking(request, userId);
            model.addAttribute("booking", confirmation);
            return "customer/booking-confirmation";
        } catch (Exception e) {
            log.error("Booking creation failed", e);
            model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Lỗi không xác định. Vui lòng thử lại.");
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
            log.info("Payment confirmed for booking: {}", bookingId);
            model.addAttribute("success", "Thanh toán thành công! Đặt xe được xác nhận.");
            model.addAttribute("bookingId", bookingId);
            return "customer/booking-success";
        } catch (Exception e) {
            log.error("Payment confirmation failed for booking: {}", bookingId, e);
            model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Lỗi thanh toán. Vui lòng thử lại.");
            return "redirect:/customer/booking/" + bookingId + "/payment";
        }
    }

    @PostMapping("/{bookingId}/cancel")
    public String cancelBookingBeforePayment(@PathVariable Integer bookingId,
                                             Authentication authentication,
                                             RedirectAttributes redirectAttributes) {
        try {
            Long userId = extractUserId(authentication);
            bookingService.cancelBookingBeforePayment(bookingId, userId);
            redirectAttributes.addFlashAttribute("success", "Đơn đặt xe đã được hủy thành công.");
            return "redirect:/customer/dashboard";
        } catch (Exception e) {
            log.error("Cancel booking failed for booking: {}", bookingId, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/dashboard";
        }
    }

    @PostMapping("/{bookingId}/cancel-paid")
    public String cancelBookingAfterPayment(@PathVariable Integer bookingId,
                                            Authentication authentication,
                                            RedirectAttributes redirectAttributes) {
        try {
            Long userId = extractUserId(authentication);
            bookingService.cancelBookingAfterPayment(bookingId, userId);
            redirectAttributes.addFlashAttribute("success", "Đơn đặt xe đã được hủy. Phí giữ chỗ không được hoàn lại.");
            return "redirect:/customer/dashboard";
        } catch (Exception e) {
            log.error("Cancel paid booking failed for booking: {}", bookingId, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/dashboard";
        }
    }

    @GetMapping("/{bookingId}/details")
    public String bookingDetails(@PathVariable Integer bookingId, Model model) {
        try {
            RentalHistoryDto booking = bookingService.getBookingDetails(bookingId);
            model.addAttribute("booking", booking);
            return "customer/booking-details";
        } catch (Exception e) {
            log.error("Error loading booking details for booking: {}", bookingId, e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/customer/dashboard";
        }
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new RuntimeException("Người dùng chưa xác thực");
        }
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }
}
