package fpt.swp391.carrentalsystem.controller.admin;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarDocument;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.CarDocumentRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.AdminCarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Admin car management
 * Handles car approval/rejection workflow
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCarControllerByThinhHT {

    private final AdminCarService adminCarService;
    private final UserRepository userRepository;
    private final CarDocumentRepository carDocumentRepository;

    /**
     * Display the car management dashboard with pending and rejected cars
     * GET /admin/cars
     * NOTE: Available cars section has been removed
     */
    @GetMapping("/cars")
    public String showCarDashboard(Model model) {
        log.info("Admin accessing car management dashboard");

        // Fetch cars by status (only PENDING and REJECTED)
        List<Car> pendingCars = adminCarService.getPendingCars();
        List<Car> rejectedCars = adminCarService.getRejectedCars();

        log.info("DEBUG - Pending cars count: {}", pendingCars.size());
        log.info("DEBUG - Rejected cars count: {}", rejectedCars.size());

        // Create a map of owner information for display
        Map<Long, User> ownerMap = new HashMap<>();

        // Helper to populate owner map
        java.util.function.Consumer<List<Car>> populateOwnerMap = (cars) -> {
            for (Car car : cars) {
                if (car.getOwnerId() != null && !ownerMap.containsKey(car.getOwnerId())) {
                    userRepository.findById(car.getOwnerId())
                            .ifPresent(user -> ownerMap.put(car.getOwnerId(), user));
                }
            }
        };

        populateOwnerMap.accept(pendingCars);
        populateOwnerMap.accept(rejectedCars);

        // Add data to model
        model.addAttribute("pendingCars", pendingCars);
        model.addAttribute("rejectedCars", rejectedCars);
        model.addAttribute("ownerMap", ownerMap);

        // Statistics
        long pendingCount = adminCarService.countPendingCars();
        long rejectedCount = adminCarService.countRejectedCars();

        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "admin/admin-car-dashboard";
    }

    /**
     * Approve a car
     * POST /admin/cars/{id}/approve
     */
    @PostMapping("/cars/{id}/approve")
    public String approveCar(@PathVariable("id") Long carId, RedirectAttributes redirectAttributes) {
        log.info("Admin approving car with ID: {}", carId);

        try {
            adminCarService.approveCar(carId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã duyệt xe thành công!");
        } catch (IllegalArgumentException e) {
            log.error("Car not found: {}", carId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Invalid car state for approval: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Error approving car: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi duyệt xe!");
        }

        return "redirect:/admin/cars";
    }

    /**
     * Reject a car
     * POST /admin/cars/{id}/reject
     */
    @PostMapping("/cars/{id}/reject")
    public String rejectCar(@PathVariable("id") Long carId, RedirectAttributes redirectAttributes) {
        log.info("Admin rejecting car with ID: {}", carId);

        try {
            adminCarService.rejectCar(carId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối xe thành công!");
        } catch (IllegalArgumentException e) {
            log.error("Car not found: {}", carId);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Invalid car state for rejection: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Error rejecting car: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi từ chối xe!");
        }

        return "redirect:/admin/cars";
    }

    /**
     * View car details with documents
     * GET /admin/cars/{id}
     */
    @GetMapping("/cars/{id}")
    public String viewCarDetail(@PathVariable("id") Long carId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Admin viewing car detail: {}", carId);

        Car car = adminCarService.getCarById(carId);

        if (car == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy xe!");
            return "redirect:/admin/cars";
        }

        // Get owner information
        User owner = null;
        if (car.getOwnerId() != null) {
            owner = userRepository.findById(car.getOwnerId()).orElse(null);
        }

        // Get car documents
        List<CarDocument> documents = carDocumentRepository.findByCarId(carId);
        log.info("Found {} documents for car {}", documents.size(), carId);

        model.addAttribute("car", car);
        model.addAttribute("owner", owner);
        model.addAttribute("documents", documents);

        return "admin/admin-car-detail";
    }
}
