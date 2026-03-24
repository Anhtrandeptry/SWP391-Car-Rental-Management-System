package fpt.swp391.carrentalsystem.dto.request;

import fpt.swp391.carrentalsystem.enums.Gender;
import fpt.swp391.carrentalsystem.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Data
public class RegisterRequest {
    @NotBlank(message = "Họ không được để trống")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String phoneNumber;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private Gender gender;

    private Role role;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    private String confirmPassword;
}

