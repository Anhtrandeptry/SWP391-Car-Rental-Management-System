package fpt.swp391.carrentalsystem.mapper.admin;

import fpt.swp391.carrentalsystem.dto.response.CustomerResponse;
import fpt.swp391.carrentalsystem.entity.User;

public class CustomerMapper {

    public static CustomerResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return CustomerResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress()) // Lấy từ trường address trong User Entity
                .gender(user.getGender()) // Lấy từ trường gender (Enum)
                .nationalId(user.getNationalId()) // Số CCCD
                .driversLicense(user.getDriversLicense()) // Bằng lái xe
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin()) // Thời gian đăng nhập cuối cùng
                .build();
    }
}