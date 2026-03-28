package fpt.swp391.carrentalsystem.controller.common;

import fpt.swp391.carrentalsystem.service.CarServiceByThanhQC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
@Slf4j
public class CarController {

    private final CarServiceByThanhQC carService;

    @GetMapping("/cars")
    public String listCars(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String carType,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String endTime,
            Model model
    ) {
        try {
            log.info("Loading car list - location: {}, startDate: {}, endDate: {}, brand: {}, seats: {}",
                    location, startDate, endDate, brand, seats);

            var cars = carService.searchCars(location, startDate, startTime, endDate, endTime, name, seats, brand, carType, fuelType);
            log.info("Found {} cars", cars != null ? cars.size() : 0);
            model.addAttribute("cars", cars);

            var locations = carService.getAllLocations();
            log.info("Available locations: {}", locations != null ? locations.size() : 0);
            model.addAttribute("locations", locations);

            model.addAttribute("brands", carService.getAllBrands());
            model.addAttribute("carTypes", carService.getAllCarTypes());
            model.addAttribute("fuelTypes", carService.getAllFuelTypes());
            model.addAttribute("seatsList", carService.getAllSeats());

            model.addAttribute("location", location);
            model.addAttribute("startDate", startDate);
            model.addAttribute("startTime", startTime);
            model.addAttribute("endDate", endDate);
            model.addAttribute("endTime", endTime);
            model.addAttribute("brand", brand);
            model.addAttribute("seats", seats);
            model.addAttribute("carType", carType);

            log.info("Car listing page loaded successfully");
            return "public/cars";
        } catch (Exception e) {
            log.error("Error loading car list: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể tải danh sách xe. Vui lòng thử lại.");
            model.addAttribute("cars", java.util.Collections.emptyList());
            model.addAttribute("locations", java.util.Collections.emptyList());
            model.addAttribute("brands", java.util.Collections.emptyList());
            model.addAttribute("carTypes", java.util.Collections.emptyList());
            model.addAttribute("fuelTypes", java.util.Collections.emptyList());
            model.addAttribute("seatsList", java.util.Collections.emptyList());
            return "public/cars";
        }
    }

    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable Integer id, Model model) {
        try {
            log.info("Loading car detail for id: {}", id);
            var car = carService.getCarById(id);

            if (car == null) {
                log.warn("Car not found with id: {}", id);
                return "redirect:/public/cars";
            }

            model.addAttribute("car", car);
            log.info("Car detail loaded successfully: {}", car.getName());
            return "public/car-detail";
        } catch (Exception e) {
            log.error("Error loading car detail for id {}: {}", id, e.getMessage(), e);
            return "redirect:/public/cars";
        }
    }
}