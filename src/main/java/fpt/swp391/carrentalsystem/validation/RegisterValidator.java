package fpt.swp391.carrentalsystem.validation;

import fpt.swp391.carrentalsystem.dto.request.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class RegisterValidator {

    public void validate(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password confirmation does not match");
        }
    }
}

