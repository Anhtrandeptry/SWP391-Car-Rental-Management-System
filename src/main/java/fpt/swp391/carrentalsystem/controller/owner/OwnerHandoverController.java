package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.Handover;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.HandoverRepository;
import fpt.swp391.carrentalsystem.service.HandoverService;
import org.springframework.security.core.Authentication;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerHandoverController {

    private final BookingRepository bookingRepository;
    private final HandoverRepository handoverRepository;
    private final HandoverService handoverService;
    private final UserRepository userRepository;

    @GetMapping("/handover-list")
    public String listPendingHandovers(Model model, Authentication authentication) {
        String email = authentication.getName();

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin chủ xe"));

        Integer ownerId = owner.getId().intValue();

        List<Booking> pendingBookings = bookingRepository.findByCar_Owner_IdAndStatus(ownerId, BookingStatus.HANDOVER_PENDING);

        model.addAttribute("bookings", pendingBookings);
        return "owner/handover-list";
    }

    @GetMapping("/handover-review/{bookingId}")
    public String reviewHandover(@PathVariable Integer bookingId, Model model) {
        Handover handover = handoverRepository.findByBooking_BookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Yêu cầu không tồn tại"));
        model.addAttribute("handover", handover);
        return "owner/handover-review";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam Integer bookingId, RedirectAttributes ra) {
        handoverService.approveHandover(bookingId);
        ra.addFlashAttribute("success", "Đã bàn giao xe thành công. Chuyến đi bắt đầu!");
        return "redirect:/owner/handover-list";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam Integer bookingId, RedirectAttributes ra) {
        handoverService.rejectHandover(bookingId);
        ra.addFlashAttribute("error", "Đã từ chối yêu cầu. Khách hàng cần thực hiện lại.");
        return "redirect:/owner/handover-list";
    }
}