package fpt.swp391.carrentalsystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarResponseDto {

    private Integer carId;
    private String name;
    private String brand;
    private String model;
    private BigDecimal pricePerDay;
    private String location;
    private Integer seats;
    private String fuelType;
}