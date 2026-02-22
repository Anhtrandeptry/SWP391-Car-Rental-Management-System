package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse; // Import mới ở đây
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerCarController {

    private final CarService carService;

    public CustomerCarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars")
    public String listCars(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String carType,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String location,
            Model model
    ) {
        List<CarListItemResponse> cars = carService.filterCars(
                name, seats, brand, carType, fuelType, location
        );

        model.addAttribute("cars", cars);

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

        return "customer/cars";
    }

    @GetMapping("/cars/{id}")
    public String carDetail(@PathVariable Long id, Model model) {
        Car car = carService.getCarById(id);

        if (car == null) {
            return "redirect:/customer/cars";
        }

        model.addAttribute("car", car);
        return "customer/car-detail";
    }
}