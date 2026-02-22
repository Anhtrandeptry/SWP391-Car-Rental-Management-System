package fpt.swp391.carrentalsystem.dto;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarListItemDto {
    private Long id;
    private String name;
    private String location;
    private BigDecimal pricePerDay;
    private String status;
    private String mainImageUrl;

    public static CarListItemDto fromEntity(Car car) {
        String mainImage = "/images/default-car.png";
        if (car.getImages() != null && !car.getImages().isEmpty()) {
            mainImage = car.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                    .map(CarImage::getImageUrl)
                    .findFirst()
                    .orElse(car.getImages().get(0).getImageUrl());
        }

        return CarListItemDto.builder()
                .id(car.getId())
                .name(car.getName())
                .location(car.getLocation())
                .pricePerDay(car.getPricePerDay())
                .status(car.getStatus())
                .mainImageUrl(mainImage)
                .build();
    }
}