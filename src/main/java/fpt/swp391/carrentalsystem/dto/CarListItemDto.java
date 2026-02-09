package fpt.swp391.carrentalsystem.dto;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarListItemDto {

    private Long id;
    private String name;
    private String brand;
    private String model;
    private String location;
    private BigDecimal pricePerDay;
    private BigDecimal averageRating;
    private String mainImageUrl;


    public static CarListItemDto fromEntity(Car car) {

        String mainImage = null;
        if (car.getImages() != null && !car.getImages().isEmpty()) {
            for (CarImage img : car.getImages()) {
                if (Boolean.TRUE.equals(img.getIsMain())) {
                    mainImage = img.getImageUrl();
                    break;
                }
            }
            if (mainImage == null) {
                mainImage = car.getImages().get(0).getImageUrl();
            }
        }

        return CarListItemDto.builder()
                .id(car.getId())
                .name(car.getName())
                .brand(car.getBrand())
                .model(car.getModel())
                .location(car.getLocation())
                .pricePerDay(car.getPricePerDay())
                .averageRating(
                        car.getAverageRating() != null
                                ? car.getAverageRating()
                                : BigDecimal.ZERO
                )
                .mainImageUrl(mainImage)
                .build();
    }
}
