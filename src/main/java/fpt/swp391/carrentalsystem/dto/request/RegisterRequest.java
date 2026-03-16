package fpt.swp391.carrentalsystem.dto.request;

import fpt.swp391.carrentalsystem.enums.Gender;
import fpt.swp391.carrentalsystem.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private Gender gender;
    private String phoneNumber;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;
}

