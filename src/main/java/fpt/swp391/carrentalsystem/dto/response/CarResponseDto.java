package fpt.swp391.carrentalsystem.dto.response;

import fpt.swp391.carrentalsystem.entity.Car;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for car response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDto {

    private Integer carId;
    private Long ownerId;

    // Basic info
    private String name;
    private String brand;
    private String model;
    private Integer year;
    private String city;
    private String licensePlate;
    private String color;
    private String carType;
    private String transmissionType;
    private String fuelType;
    private String fuelConsumption;
    private Integer seats;
    private BigDecimal pricePerDay;
    private BigDecimal estimatedIncome;
    private String description;

    // Location
    private String location;
    private String address;
    private String province;
    private String district;
    private String ward;
    private String latitude;
    private String longitude;

    // Technical
    private LocalDate registrationDate;
    private Integer mileage;
    private String condition;
    private Integer engineCapacity;

    // Amenities
    private Boolean hasAirConditioner;
    private Boolean hasDashCam;
    private Boolean hasReverseCamera;
    private Boolean hasGPS;
    private Boolean hasUSB;
    private Boolean hasBluetooth;
    private Boolean hasMaps;
    private Boolean has360Camera;
    private Boolean hasSpareWheel;
    private Boolean hasDVDPlayer;
    private Boolean hasETC;
    private Boolean hasSunroof;

    // Images
    private String coverImage;
    private List<String> images;

    // Policies
    private Integer dailyKmLimit;
    private BigDecimal overLimitFee;
    private String fuelPolicy;
    private String cancellationPolicy;
    private String deliveryTime;
    private String deliveryLocation;

    // Additional
    private String specialNotes;
    private String renterRequirements;
    private String cleaningPolicy;

    // Status
    private String status;
    private BigDecimal averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Owner info
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;

    // For car availability management (transient - not from DB)
    private boolean hasActiveBooking;

    /**
     * Constructor to create DTO from Car entity
     */
    public CarResponseDto(Car car) {
        if (car == null) return;

        this.carId = car.getCarId();
        this.ownerId = car.getOwnerId();
        this.name = car.getName();
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.year = car.getYear();
        this.city = car.getCity();
        this.licensePlate = car.getLicensePlate();
        this.color = car.getColor();
        this.carType = car.getCarType();
        this.transmissionType = car.getTransmissionType();
        this.fuelType = car.getFuelType() != null ? car.getFuelType().name() : null;
        this.fuelConsumption = car.getFuelConsumption();
        this.seats = car.getSeats();
        this.pricePerDay = car.getPricePerDay();
        this.estimatedIncome = car.getEstimatedIncome();
        this.description = car.getDescription();
        this.location = car.getLocation();
        this.address = car.getAddress();
        this.province = car.getProvince();
        this.district = car.getDistrict();
        this.ward = car.getWard();
        this.latitude = car.getLatitude();
        this.longitude = car.getLongitude();
        this.registrationDate = car.getRegistrationDate();
        this.mileage = car.getMileage();
        this.condition = car.getCondition();
        this.engineCapacity = car.getEngineCapacity();
        this.hasAirConditioner = car.getHasAirConditioner();
        this.hasDashCam = car.getHasDashCam();
        this.hasReverseCamera = car.getHasReverseCamera();
        this.hasGPS = car.getHasGPS();
        this.hasUSB = car.getHasUSB();
        this.hasBluetooth = car.getHasBluetooth();
        this.hasMaps = car.getHasMaps();
        this.has360Camera = car.getHas360Camera();
        this.hasSpareWheel = car.getHasSpareWheel();
        this.hasDVDPlayer = car.getHasDVDPlayer();
        this.hasETC = car.getHasETC();
        this.hasSunroof = car.getHasSunroof();
        this.coverImage = car.getCoverImage();
        this.dailyKmLimit = car.getDailyKmLimit();
        this.overLimitFee = car.getOverLimitFee();
        this.fuelPolicy = car.getFuelPolicy();
        this.cancellationPolicy = car.getCancellationPolicy();
        this.deliveryTime = car.getDeliveryTime();
        this.deliveryLocation = car.getDeliveryLocation();
        this.specialNotes = car.getSpecialNotes();
        this.renterRequirements = car.getRenterRequirements();
        this.cleaningPolicy = car.getCleaningPolicy();
        this.status = car.getStatus() != null ? car.getStatus().name() : null;
        this.averageRating = car.getAverageRating();
        this.createdAt = car.getCreatedAt();
        this.updatedAt = car.getUpdatedAt();

        // Owner info
        if (car.getOwner() != null) {
            this.ownerName = car.getOwner().getFirstName() + " " + car.getOwner().getLastName();
            this.ownerPhone = car.getOwner().getPhoneNumber();
            this.ownerEmail = car.getOwner().getEmail();
        }
    }
}
