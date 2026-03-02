package fpt.swp391.carrentalsystem.dto.response;

import fpt.swp391.carrentalsystem.enums.Gender;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerResponse {
    private Long id;
    private String fullName; // Gộp First + Last Name
    private String email;
    private String phoneNumber;
    private int numberOfCars; // Cột "Số xe"
    private double totalRevenue; // Cột "Tổng doanh thu"
    private UserStatus status;
    private LocalDateTime createdAt;
    private String address;
    private Gender gender;
}
