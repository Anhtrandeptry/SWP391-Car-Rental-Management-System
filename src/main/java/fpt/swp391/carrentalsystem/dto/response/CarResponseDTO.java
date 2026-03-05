package fpt.swp391.carrentalsystem.dto.response;


import fpt.swp391.carrentalsystem.dto.request.CarDocumentDTO;
import fpt.swp391.carrentalsystem.entity.Car;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDTO {

    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String city;
    private String licensePlate;
    private String color;
    private String transmissionType;
    private String fuelType;
    private Integer seats;
    private BigDecimal pricePerDay;
    private BigDecimal estimatedIncome;
    private String status;
    private String description;
    private String imageUrl;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CarDocumentDTO carDocument;

    // Constructor từ Entity
    public CarResponseDTO(Car car) {
        this.id = car.getId();
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.year = car.getYear();
        this.city = car.getCity();
        this.licensePlate = car.getLicensePlate();
        this.color = car.getColor();
        this.transmissionType = car.getTransmissionType();
       // this.fuelType = car.getFuelType();
        this.seats = car.getSeats();
        this.pricePerDay = car.getPricePerDay();
        this.estimatedIncome = car.getEstimatedIncome();
        this.status = car.getStatus() != null ? car.getStatus().name() : null;
        this.description = car.getDescription();
        //this.imageUrl = car.getImageUrl();
        this.ownerId = car.getOwnerId();
        this.createdAt = car.getCreatedAt();
        this.updatedAt = car.getUpdatedAt();
    }
}
