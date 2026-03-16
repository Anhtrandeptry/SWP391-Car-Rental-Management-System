package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.CarReturn;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.carReturn.CarReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/owner/return")
@RequiredArgsConstructor
public class OwnerReturnController {

    private final CarReturnService carReturnService;
    private final BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list-car-return")
    public String getReturnHistory(Principal principal, Model model) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy ID của Owner (ép kiểu Integer nếu cần thiết cho Repository)
        Integer ownerId = user.getId().intValue();

        // 1. Danh sách xe đã được trả thuộc sở hữu của Owner này
        List<CarReturn> returnList = carReturnService.getCarReturnsByOwner(ownerId);

        // 2. (Tùy chọn) Danh sách Booking đã hoàn thành chuyến đi nhưng chưa làm thủ tục trả xe
        // Bạn có thể cần viết thêm hàm getBookingsReadyToReturnByOwner nếu cần tính năng này cho Owner
        // List<Booking> pendingBookings = carReturnService.getBookingsReadyToReturnByOwner(ownerId);

        model.addAttribute("returnList", returnList);
        model.addAttribute("ownerId", ownerId);

        return "owner/car-return-management";
    }

    @GetMapping("/confirm-detail/{id}")
    public String getConfirmDetailPage(@PathVariable("id") Integer returnId, Model model) {
        CarReturn carReturn = carReturnService.getCarReturnDetail(returnId);

        // Nếu đơn này đã được duyệt rồi thì không cho vào trang confirm nữa, chuyển sang trang detail
        if (Boolean.TRUE.equals(carReturn.getOwnerConfirmation())) {
            return "redirect:/owner/return/detail/" + returnId;
        }

        model.addAttribute("carReturn", carReturn);
        return "owner/confirm-return-form"; // Trang form mà tôi đã thiết kế cho bạn ở phía trên
    }

    @GetMapping("/calculate-penalty/{id}")
    public String getPenaltyCalculationPage(@PathVariable("id") Integer returnId, Model model) {
        // Lấy dữ liệu
        CarReturn carReturn = carReturnService.getCarReturnDetail(returnId);

        // Nếu đơn đã xác nhận rồi thì không cho tính lại (redirect về trang detail)
        if (Boolean.TRUE.equals(carReturn.getOwnerConfirmation())) {
            return "redirect:/owner/return/detail/" + returnId;
        }

        model.addAttribute("carReturn", carReturn);

        // Trả về trang HTML chuyên dùng để tính toán (trang mới bạn định tạo)
        return "owner/penalty-calculator";
    }

    @PostMapping("/save-penalty")
    public String savePenalty(@RequestParam("returnId") Integer returnId,
                              @RequestParam("penaltyAmount") BigDecimal penaltyAmount,
                              RedirectAttributes redirectAttributes) {
        try {
            // 1. Lấy thông tin đơn trả xe hiện tại
            CarReturn carReturn = carReturnService.getCarReturnDetail(returnId);

            // 2. Cập nhật số tiền phạt (Bao gồm phạt muộn + phạt thủ công đã cộng dồn từ JS)
            carReturn.setPenaltyAmount(penaltyAmount);

            // 3. (Tùy chọn) Đánh dấu là Owner đã xác nhận chi phí
            // carReturn.setOwnerConfirmation(true);

            // 4. Lưu vào Database
            carReturnService.updatePenaltyAmount(returnId, penaltyAmount);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Đã xác nhận và lưu phí phạt thành công!");

            // Chuyển hướng về trang danh sách hoặc trang chi tiết xác nhận
            return "redirect:/owner/return/confirm-detail/" + returnId;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi lưu phí phạt!");
            return "redirect:/owner/return/calculate-penalty/" + returnId;
        }
    }// Thêm vào OwnerReturnController.java
    @PostMapping("/confirm/{id}")
    public String confirmReturn(@PathVariable("id") Integer returnId, RedirectAttributes redirectAttributes) {
        try {
            carReturnService.confirmCarReturn(returnId);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xác nhận trả xe thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi khi xác nhận: " + e.getMessage());
        }
        return "redirect:/owner/return/list-car-return";
    }

    @GetMapping("/detail/{id}")
    public String getReturnDetailPage(@PathVariable("id") Integer returnId, Model model) {
        CarReturn carReturn = carReturnService.getCarReturnDetail(returnId);
        model.addAttribute("carReturn", carReturn);
        return "owner/car-return-detail"; // Tên file HTML sẽ tạo ở bước dưới
    }
}