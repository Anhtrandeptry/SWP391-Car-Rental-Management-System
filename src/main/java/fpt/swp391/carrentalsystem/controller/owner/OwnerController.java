package fpt.swp391.carrentalsystem.controller.owner;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    @GetMapping("/create-car-step1")
    public String createCarStep1() {

        return "owner/create-car-step1";
    }
}



//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/owner")
//public class OwnerController {
//
//    @GetMapping("/dashboard")
//    public String dashboard() {
//        return "owner/car-owner-dashboard";
//    }
//}

