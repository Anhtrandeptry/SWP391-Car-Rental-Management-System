package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarListItemResponse {
    private Long id;
    private String name;
    private String location;
    private String brand;
    private String model;
    private BigDecimal pricePerDay;
    private String status;
    private String mainImageUrl;
    private Integer totalTrips;
    private Double averageRating;
}