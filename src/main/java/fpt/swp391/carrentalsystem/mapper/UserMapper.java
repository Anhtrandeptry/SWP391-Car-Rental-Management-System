package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.request.RegisterRequest;
import fpt.swp391.carrentalsystem.entity.User;

public class UserMapper {

    public static User toEntity(RegisterRequest dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return user;
    }
}

