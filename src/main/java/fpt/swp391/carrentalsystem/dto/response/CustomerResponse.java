package fpt.swp391.carrentalsystem.dto.response;

import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.enums.Gender;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private String nationalId;
    private String driversLicense;
    private String avatarUrl;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Hàm tiện ích để hiển thị tên đầy đủ trên giao diện
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}