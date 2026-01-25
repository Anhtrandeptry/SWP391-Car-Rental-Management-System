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

    /**
     * Show car list page. Optional query param `name` will filter cars by name (case-insensitive, contains).
     */
    @GetMapping("/cars")
    public String listCars(@RequestParam(name = "name", required = false) String name, Model model) {
        List<CarListItemDto> cars;
        if (name == null || name.isBlank()) {
            cars = carService.listAll();
        } else {
            cars = carService.searchByName(name);
        }
        model.addAttribute("cars", cars);
        model.addAttribute("name", name == null ? "" : name);
        return "public/cars";
    }
}
