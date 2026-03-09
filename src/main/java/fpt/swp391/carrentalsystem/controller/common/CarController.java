package fpt.swp391.carrentalsystem.controller.common;

import fpt.swp391.carrentalsystem.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

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
        model.addAttribute("cars", carService.searchCars(location, startDate, startTime, endDate, endTime, name, seats, brand, carType, fuelType));

        model.addAttribute("locations", carService.getAllLocations());
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

        return "public/cars";
    }
}