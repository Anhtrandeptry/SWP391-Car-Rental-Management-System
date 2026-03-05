package fpt.swp391.carrentalsystem.controller.owner;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class OwnerController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "owner/car-owner-dashboard";
    }


    @GetMapping("/create-car-step1")
    public String step1() {
        return "owner/create-car-step1";
    }


    @GetMapping("/create-car-step2")
    public String step2() {
        return "owner/create-car-step2";
    }

    @GetMapping("/create-car-step3")
    public String step3() {
        return "owner/create-car-step3";
    }
    @GetMapping("/owner-car-list")
    public String listCars() {
        return "owner/owner-car-list"; // Phải khớp chính xác đường dẫn file html
    }
}





