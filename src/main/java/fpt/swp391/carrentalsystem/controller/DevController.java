package fpt.swp391.carrentalsystem.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dev")
public class DevController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @GetMapping("/bcrypt")
    public String bcrypt(@RequestParam String raw) {
        return encoder.encode(raw);
    }
}
