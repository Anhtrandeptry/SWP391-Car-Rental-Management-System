package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.service.HandoverService;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import fpt.swp391.carrentalsystem.enums.BookingStatus;


@Controller
@RequestMapping("/customer/handover")
@RequiredArgsConstructor
public class CustomerHandoverController {

    private final HandoverService handoverService;
    private final BookingRepository bookingRepository;


    @GetMapping("/list")
    public String listHandoverBookings(Model model) {
        List<BookingStatus> statuses = Arrays.asList(BookingStatus.ACCEPTED, BookingStatus.HANDOVER_PENDING);
        List<Booking> bookings = bookingRepository.findByStatusIn(statuses);

        model.addAttribute("bookings", bookings);
        return "customer/handover-list";
    }

    @GetMapping("/{bookingId}")
    public String showHandoverPage(@PathVariable("bookingId") Integer bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        model.addAttribute("booking", booking);
        return "customer/handover";
    }

    @PostMapping("/submit")
    public String submitHandoverRequest(@RequestParam("bookingId") Integer bookingId,
                                        @RequestParam("fuelLevel") Integer fuelLevel,
                                        @RequestParam("odometer") Integer odometer,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam("images") List<MultipartFile> images,
                                        RedirectAttributes ra) {
        try {
            handoverService.createHandoverRequest(bookingId, fuelLevel, odometer, description, images);

            ra.addFlashAttribute("successMsg", "Yêu cầu bàn giao đã được gửi thành công. Vui lòng đợi Chủ xe xác nhận!");
            return "redirect:/customer/handover/list";

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMsg", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/customer/handover/" + bookingId;
        }
    }
}