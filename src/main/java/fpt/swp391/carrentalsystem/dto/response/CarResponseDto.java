package fpt.swp391.carrentalsystem.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarResponseDto {

    private Integer carId;
    private String name;
    private String brand;
    private String model;
    private String carType;
    private BigDecimal pricePerDay;
    private String location;
    private Integer seats;
    private String fuelType;
    private String licensePlate;
    private String status;
    private String description;
    private BigDecimal averageRating;

    // Owner info
    private String ownerName;
    private String ownerPhone;

    // For car availability management (transient - not from DB)
    private boolean hasActiveBooking;
}