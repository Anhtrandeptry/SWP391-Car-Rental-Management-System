package fpt.swp391.carrentalsystem.dto.request;

import fpt.swp391.carrentalsystem.enums.Gender;
import fpt.swp391.carrentalsystem.enums.Role;
import lombok.Data;

@Data
public class CreateUserForm {

    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String password;
}