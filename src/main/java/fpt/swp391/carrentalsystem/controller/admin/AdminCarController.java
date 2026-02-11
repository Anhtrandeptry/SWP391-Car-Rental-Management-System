package fpt.swp391.carrentalsystem.controller.admin;

import fpt.swp391.carrentalsystem.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminCarController {

    private final CarService carService;

    public AdminCarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/car-management")
    public String carManagement(Model model) {
        model.addAttribute("cars", carService.getPendingCars());
        return "admin/car-management";
    }

    @PostMapping("/approve/{id}")
    public String approveCar(@PathVariable Long id) {
        carService.approveCar(id);
        return "redirect:/admin/car-management";
    }

    @PostMapping("/reject/{id}")
    public String rejectCar(@PathVariable Long id) {
        carService.rejectCar(id);
        return "redirect:/admin/car-management";
    }
}