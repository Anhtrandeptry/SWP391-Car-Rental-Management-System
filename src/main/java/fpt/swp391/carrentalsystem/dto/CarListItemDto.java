package fpt.swp391.carrentalsystem.dto;

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
}
