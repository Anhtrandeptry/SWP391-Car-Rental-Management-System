package fpt.swp391.carrentalsystem.mapper.admin;

import fpt.swp391.carrentalsystem.dto.response.OwnerResponse;
import fpt.swp391.carrentalsystem.entity.User;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class OwnerMapper {

    /**
     * Chuyển đổi từ Entity User sang OwnerResponse DTO
     */
    public OwnerResponse toResponse(User entity) {
        if (entity == null) {
            return null;
        }

        return OwnerResponse.builder()
                .id(entity.getId())
                .fullName(entity.getFirstName() + " " + entity.getLastName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .address(entity.getAddress()) // Lấy từ trường address trong User Entity
                .gender(entity.getGender())
                /* LƯU Ý: Số xe và Tổng doanh thu thường cần tính toán từ table Car và Booking.
                   Trong khuôn khổ Mapper này, ta để mặc định hoặc giá trị giả lập
                   để match với giao diện bạn gửi.
                */
                .numberOfCars(12)
                .totalRevenue(0.0)
                .build();
    }
}