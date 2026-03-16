package fpt.swp391.carrentalsystem.controller.common;

import fpt.swp391.carrentalsystem.dto.request.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class HomeController {
    @GetMapping("/home")
    public String homePage() {
        return "public/home";
    }


}
