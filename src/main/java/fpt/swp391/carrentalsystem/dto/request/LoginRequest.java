package fpt.swp391.carrentalsystem.dto.request;

import lombok.*;

@Data
public class LoginRequest {
    private String username; // email hoặc phone
    private String password;
}

