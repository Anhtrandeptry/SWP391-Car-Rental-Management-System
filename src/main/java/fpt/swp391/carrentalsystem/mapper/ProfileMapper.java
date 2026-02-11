package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.UpdateProfileRequest;
import fpt.swp391.carrentalsystem.dto.UserProfile;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * MapStruct mapper for profile-related conversions between entities and DTOs.
 * Handles conversion between User entity and UserProfile/UpdateProfileRequest DTOs.
 */
@Mapper(componentModel = "spring")
public interface ProfileMapper {

    /**
     * Convert User entity to UserProfile DTO.
     * Maps the Gender enum to its string name.
     *
     * @param user the User entity to convert
     * @return the UserProfile DTO
     */
    @Mapping(target = "gender", source = "gender", qualifiedByName = "genderToString")
    UserProfile userToUserProfile(User user);

    /**
     * Convert UpdateProfileRequest DTO to User entity.
     * Maps the gender string to Gender enum.
     * This is used for updating user profile information.
     *
     * @param request the UpdateProfileRequest DTO
     * @param user the User entity to update (target)
     */
    @Mapping(target = "gender", source = "gender", qualifiedByName = "stringToGender")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    void updateProfileRequestToUser(UpdateProfileRequest request, @MappingTarget User user);

    /**
     * Convert Gender enum to String (name).
     *
     * @param gender the Gender enum
     * @return the string representation of the gender, or null if gender is null
     */
    @Named("genderToString")
    default String genderToString(Gender gender) {
        return gender == null ? null : gender.name();
    }

    /**
     * Convert String to Gender enum.
     * Safely handles invalid gender values by returning null.
     *
     * @param genderString the string representation of gender
     * @return the Gender enum, or null if the string is null or invalid
     */
    @Named("stringToGender")
    default Gender stringToGender(String genderString) {
        if (genderString == null || genderString.isBlank()) {
            return null;
        }
        try {
            return Gender.valueOf(genderString);
        } catch (IllegalArgumentException e) {
            // Invalid gender value - return null instead of throwing exception
            return null;
        }
    }
}

