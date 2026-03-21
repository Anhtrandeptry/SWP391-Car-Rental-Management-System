package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.request.RegisterRequest;
import fpt.swp391.carrentalsystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for User entity conversions.
 * Handles conversion from DTOs to User entity.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convert RegisterRequest DTO to User entity.
     * Password hash is not set by the mapper - it should be set separately by the service.
     *
     * @param dto the RegisterRequest DTO
     * @return the User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User registerRequestToUser(RegisterRequest dto);
}

