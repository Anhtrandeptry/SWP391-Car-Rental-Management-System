package fpt.swp391.carrentalsystem.mapper;

import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(target = "mainImageUrl", source = "images", qualifiedByName = "mapMainImage")
    CarListItemResponse toListItemResponse(Car car);

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
}