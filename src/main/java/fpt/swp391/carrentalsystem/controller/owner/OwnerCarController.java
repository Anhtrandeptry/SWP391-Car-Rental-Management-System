package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import fpt.swp391.carrentalsystem.service.CarService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class OwnerCarController {

    private final CarService carService;

    public OwnerCarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/my-cars")
    public String viewMyCars(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        Long ownerId = currentUser.getUser().getId();
        model.addAttribute("cars", carService.getCarsByOwner(ownerId));
        return "owner/my-cars";
    }
}