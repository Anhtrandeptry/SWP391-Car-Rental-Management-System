package fpt.swp391.carrentalsystem.controller.customer;


import fpt.swp391.carrentalsystem.dto.request.CarReturnDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.CarReturn;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CustomerRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.carReturn.CarReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/customer/return")
@RequiredArgsConstructor
public class CustomerReturnController {

    private final CarReturnService carReturnService;
    private final BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository; // Tiêm vào đây

    @GetMapping("/list-car-return")
    public String getReturnHistory(Principal principal, Model model) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long customerId = user.getId();

        // 1. Danh sách đã trả xong (Lịch sử)
        List<CarReturn> returnList = carReturnService.getCarReturnsByCustomer(customerId);

        // 2. Danh sách CHỜ trả (Dùng cho Modal tạo mới)
        List<Booking> pendingBookings = carReturnService.getBookingsReadyToReturn(customerId);

        model.addAttribute("returnList", returnList);
        model.addAttribute("pendingBookings", pendingBookings); // Đưa thêm danh sách này vào
        model.addAttribute("customerId", customerId);

        return "customer/car-return-management";
    }

    // ===== THÊM METHOD NÀY =====
    @GetMapping("/create-form")
    public String showCreateReturnForm(@RequestParam("bookingId") Integer bookingId, Principal principal, Model model) {
        // 1. Lấy thông tin user (để bảo mật, kiểm tra xem booking có thuộc về user này không)
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Lấy thông tin đầy đủ của đơn Booking thông qua Service vừa viết
        Booking booking = carReturnService.getBookingById(bookingId);

        // 3. Khởi tạo DTO để map với form, gán sẵn bookingId vào DTO
        CarReturnDto carReturnDto = CarReturnDto.builder()
                .bookingId(bookingId)
                .actualReturnDate(LocalDateTime.now()) // Gợi ý giờ trả là hiện tại
                .build();

        // 4. Đưa dữ liệu vào Model
        model.addAttribute("booking", booking);        // Hiển thị thông tin tĩnh lên form
        model.addAttribute("carReturnDto", carReturnDto); // Bind dữ liệu cho input form

        return "customer/create-car-return";
    }

    @PostMapping("/confirm")
    public String confirmReturn(@ModelAttribute CarReturnDto carReturnDto,
                                RedirectAttributes redirectAttributes) {

        try {

            carReturnService.createCarReturn(carReturnDto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo đơn trả xe thành công!");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi khi tạo đơn trả xe!");

            e.printStackTrace();
        }

        return "redirect:/customer/return/list-car-return";
    }

    @GetMapping("/detail/{id}")
    public String getReturnDetail(@PathVariable("id") Integer returnId, Principal principal, Model model) {
        // 1. Lấy thông tin User hiện tại
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Lấy dữ liệu chi tiết từ Service
        CarReturn carReturn = carReturnService.getCarReturnDetail(returnId);

        // 3. Kiểm tra bảo mật (Security Check)
        // Đảm bảo đơn trả xe này thuộc về khách hàng đang đăng nhập
        if (!carReturn.getBooking().getCustomer().getId().equals(user.getId())) {
            return "redirect:/customer/return/list-car-return?error=access-denied";
        }

        // 4. Đưa dữ liệu qua Model
        model.addAttribute("carReturn", carReturn);
        model.addAttribute("booking", carReturn.getBooking());

        return "customer/car-return-detail"; // Đường dẫn tới file HTML chi tiết
    }


}