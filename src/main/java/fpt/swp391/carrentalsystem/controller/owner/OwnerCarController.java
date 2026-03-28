package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import fpt.swp391.carrentalsystem.service.CarServiceByThanhQC;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/owner")
public class OwnerCarController {

    private final CarServiceByThanhQC carService;

    public OwnerCarController(CarServiceByThanhQC carService) {
        this.carService = carService;
    }

    @GetMapping("/my-cars")
    public String viewMyCars(@AuthenticationPrincipal CustomUserDetails currentUser,
                             @RequestParam(required = false, defaultValue = "all") String filter,
                             Model model) {
        Long ownerId = currentUser.getUser().getId();

        model.addAttribute("cars", carService.getCarsByOwnerAndStatus(ownerId, filter));
        model.addAttribute("currentFilter", filter);

        return "owner/my-cars";
    }

    @GetMapping("/car-detail/{id}")
    public String viewCarDetail(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        if (car == null) {
            return "redirect:/owner/my-cars?error=notfound";
        }
        model.addAttribute("car", car);
        return "owner/owner-car-detail";
    }

    @PostMapping("/delete-car/{id}")
    public String deleteCar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            carService.updateCarStatus(id, CarStatus.INACTIVE);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa xe thành công (Ngừng hoạt động)");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa xe: " + e.getMessage());
        }
        return "redirect:/owner/my-cars";
    }
}