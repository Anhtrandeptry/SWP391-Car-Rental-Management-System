package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(target = "id", source = "carId")
    @Mapping(target = "mainImageUrl", source = "images", qualifiedByName = "mapMainImage")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "averageRating", source = "averageRating", qualifiedByName = "mapRating")
    @Mapping(target = "totalTrips", constant = "0")
    CarListItemResponse toListItemResponse(Car car);

    @Mapping(source = "owner.firstName", target = "ownerName")
    @Mapping(source = "owner.phoneNumber", target = "ownerPhone")
    CarResponseDto toDto(Car car);

    @Named("mapMainImage")
    default String mapMainImage(List<CarImage> images) {
        if (images == null || images.isEmpty()) {
            return "/images/default-car.png";
        }
        return images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                .map(CarImage::getImageUrl)
                .findFirst()
                .orElse(images.get(0).getImageUrl());
    }

    @Named("mapStatus")
    default String mapStatus(CarStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("mapRating")
    default Double mapRating(java.math.BigDecimal rating) {
        return rating != null ? rating.doubleValue() : 0.0;
    }

    List<CarResponseDto> toDtoList(List<Car> cars);
}