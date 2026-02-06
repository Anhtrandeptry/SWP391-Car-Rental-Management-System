package fpt.swp391.carrentalsystem.controller.common;

import fpt.swp391.carrentalsystem.dto.CarListItemDto;
import fpt.swp391.carrentalsystem.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/public")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
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

        List<CarListItemDto> cars = carService.filterCars(
                name, seats, brand, carType, fuelType, location
        );

        model.addAttribute("cars", cars);
        model.addAttribute("name", name);
        model.addAttribute("seats", seats);
        model.addAttribute("brand", brand);
        model.addAttribute("carType", carType);
        model.addAttribute("fuelType", fuelType);
        model.addAttribute("location", location);

        return "public/cars";
    }
}
