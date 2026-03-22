package fpt.swp391.carrentalsystem.controller.common;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicPageController {

    @GetMapping("/income-estimate")
    public String incomeEstimatePage() {
        return "public/income-estimate";
    }

    @GetMapping({"/", "/home"})
    public String homePage() {
        return "public/home";
    }
}

