package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.service.CarServiceByThanhQC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerCarController {

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
        List<CarListItemResponse> cars = carService.searchCars(
                location, startDate, startTime, endDate, endTime, name, seats, brand, carType, fuelType
        );

        model.addAttribute("cars", cars);

        model.addAttribute("locations", carService.getAllLocations());
        model.addAttribute("brands", carService.getAllBrands());
        model.addAttribute("carTypes", carService.getAllCarTypes());
        model.addAttribute("fuelTypes", carService.getAllFuelTypes());
        model.addAttribute("seatsList", carService.getAllSeats());

        model.addAttribute("name", name);
        model.addAttribute("seats", seats);
        model.addAttribute("brand", brand);
        model.addAttribute("carType", carType);
        model.addAttribute("fuelType", fuelType);
        model.addAttribute("location", location);
        model.addAttribute("startDate", startDate);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endDate", endDate);
        model.addAttribute("endTime", endTime);

        return "customer/cars";
    }

    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable Integer id, Model model) {
        Car car = carService.getCarById(id);
        if (car == null) return "redirect:/customer/cars";
        model.addAttribute("car", car);
        return "customer/car-detail";
    }
}