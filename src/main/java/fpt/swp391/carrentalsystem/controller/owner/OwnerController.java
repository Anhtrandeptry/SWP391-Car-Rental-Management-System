package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.OwnerIncomeDto;
import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import fpt.swp391.carrentalsystem.service.BookingService;
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
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            Long ownerId = extractUserId(authentication);

            log.info("Loading owner dashboard for ownerId: {}", ownerId);

            // Get owner's cars
            List<CarResponseDto> cars = ownerService.getOwnerCars(ownerId);
            model.addAttribute("cars", cars);

            // Calculate statistics
            int totalCars = cars != null ? cars.size() : 0;
            model.addAttribute("totalCars", totalCars);
            log.info("Total cars: {}", totalCars);

            // Get rental history
            List<RentalHistoryDto> rentalHistory = ownerService.getRentalHistory(ownerId);
            model.addAttribute("rentalHistory", rentalHistory);

            int totalBookings = rentalHistory != null ? rentalHistory.size() : 0;
            model.addAttribute("totalBookings", totalBookings);
            log.info("Total bookings: {}", totalBookings);

            // Calculate average rating from cars
            double avgRating = 0.0;
            if (cars != null && !cars.isEmpty()) {
                avgRating = cars.stream()
                    .filter(car -> car.getAverageRating() != null)
                    .mapToDouble(car -> car.getAverageRating().doubleValue())
                    .average()
                    .orElse(0.0);
            }
            model.addAttribute("avgRating", String.format("%.1f", avgRating));
            log.info("Average rating: {}", avgRating);

            // Calculate total income from rental history
            double totalIncome = 0.0;
            if (rentalHistory != null) {
                totalIncome = rentalHistory.stream()
                    .filter(rental -> rental.getRentalFee() != null)
                    .mapToDouble(rental -> rental.getRentalFee().doubleValue())
                    .sum();
            }
            model.addAttribute("totalIncome", totalIncome);
            log.info("Total income: {}", totalIncome);

        } catch (Exception e) {
            log.error("Error loading owner dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading dashboard data");
            // Provide default values to prevent template errors
            model.addAttribute("totalCars", 0);
            model.addAttribute("totalBookings", 0);
            model.addAttribute("avgRating", "0.0");
            model.addAttribute("totalIncome", 0.0);
        }
        return "owner/car-owner-dashboard-by-danhtdt";
    }

    @GetMapping("/booking-history")
    public String bookingHistory(Authentication authentication, Model model) {
        try {
            Long ownerId = extractUserId(authentication);

            // Get booking history for owner's cars
            List<RentalHistoryDto> bookingHistory = bookingService.getOwnerRentalHistory(ownerId);
            model.addAttribute("bookingHistory", bookingHistory);

        } catch (Exception e) {
            log.error("Error loading owner booking history: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading booking history");
        }
        return "owner/booking-history";
    }

    @GetMapping("/booking/{bookingId}")
    public String bookingDetail(@PathVariable Integer bookingId,
                               Authentication authentication,
                               Model model) {
        try {
            Long ownerId = extractUserId(authentication);
            RentalHistoryDto booking = bookingService.getBookingDetailsForOwner(bookingId, ownerId);
            model.addAttribute("booking", booking);

            return "owner/booking-details";
        } catch (Exception e) {
            log.error("Error loading booking detail: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "redirect:/owner/booking-history";
        }
    }

    @GetMapping("/car-availability")
    public String carAvailabilityPage(Authentication authentication, Model model) {
        try {
            Long ownerId = extractUserId(authentication);

            // Get owner's cars with availability info
            List<CarResponseDto> cars = ownerService.getOwnerCars(ownerId);

            // Add info about which cars have active bookings (IN_USE)
            for (CarResponseDto car : cars) {
                boolean hasActiveBooking = bookingRepository.hasCarInUseBooking(car.getCarId());
                car.setHasActiveBooking(hasActiveBooking);
            }

            model.addAttribute("cars", cars);

        } catch (Exception e) {
            log.error("Error loading car availability page: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading car availability data");
        }
        return "owner/car-availability";
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
        return "redirect:/owner/car-availability";
    }

    @PostMapping("/car/{carId}/set-unavailable")
    public String setCarUnavailable(@PathVariable Integer carId,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            Long ownerId = extractUserId(authentication);

            // Check if car has active booking (IN_USE) - additional validation
            if (bookingRepository.hasCarInUseBooking(carId)) {
                redirectAttributes.addFlashAttribute("error",
                        "Không thể tạm dừng cho thuê xe đang được sử dụng. Vui lòng đợi khách trả xe.");
                return "redirect:/owner/car-availability";
            }

            ownerService.setCarAvailability(carId, ownerId, CarStatus.UNAVAILABLE);
            redirectAttributes.addFlashAttribute("success", "Xe đã được tạm dừng cho thuê.");
        } catch (Exception e) {
            log.error("Error setting car unavailable: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/owner/car-availability";
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

