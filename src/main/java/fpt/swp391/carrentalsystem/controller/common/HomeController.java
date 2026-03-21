package fpt.swp391.carrentalsystem.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class HomeController {
    @GetMapping("/home")
    public String homePage() {
        return "public/home";
    }


}
