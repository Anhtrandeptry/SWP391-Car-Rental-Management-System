package fpt.swp391.carrentalsystem.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping("/income-estimate")
    public String incomeEstimate() {
        return
                "public/income-estimate";
    }
}

