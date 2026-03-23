package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.OwnerIncomeDto;
import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            Long ownerId = extractUserId(authentication);

            // Get rental history
            List<RentalHistoryDto> rentalHistory = ownerService.getRentalHistory(ownerId);
            model.addAttribute("rentalHistory", rentalHistory);

            // Get owner's cars
            List<CarResponseDto> cars = ownerService.getOwnerCars(ownerId);
            model.addAttribute("cars", cars);

        } catch (Exception e) {
            log.error("Error loading owner dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading dashboard data");
        }
        return "car-owner-dashboard-by-danhtdt";
    }

    @GetMapping("/income")
    public String incomePage(Authentication authentication, Model model) {
        try {
            Long ownerId = extractUserId(authentication);
            OwnerIncomeDto incomeDetails = ownerService.getIncomeDetails(ownerId);
            model.addAttribute("income", incomeDetails);
        } catch (Exception e) {
            log.error("Error loading income page: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading income data");
        }
        return "owner/income";
    }

    @PostMapping("/car/{carId}/set-available")
    public String setCarAvailable(@PathVariable Integer carId,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            Long ownerId = extractUserId(authentication);
            ownerService.setCarAvailability(carId, ownerId, CarStatus.AVAILABLE);
            redirectAttributes.addFlashAttribute("success", "Xe đã được mở cho thuê.");
        } catch (Exception e) {
            log.error("Error setting car available: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    @PostMapping("/car/{carId}/set-unavailable")
    public String setCarUnavailable(@PathVariable Integer carId,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            Long ownerId = extractUserId(authentication);
            ownerService.setCarAvailability(carId, ownerId, CarStatus.UNAVAILABLE);
            redirectAttributes.addFlashAttribute("success", "Xe đã được tạm dừng cho thuê.");
        } catch (Exception e) {
            log.error("Error setting car unavailable: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new RuntimeException("User not authenticated");
        }
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}

