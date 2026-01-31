package fpt.swp391.carrentalsystem.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    // ========== STEP 1: Thiết Lập ==========
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "license_plate", unique = true, length = 20)
    private String licensePlate;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "car_type", length = 50)
    private String carType;

    @Column(name = "transmission_type", length = 50)
    private String transmissionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Column(name = "fuel_consumption", length = 50)
    private String fuelConsumption;

    @Column(name = "seats")
    private Integer seats;

    @Column(name = "price_per_day", precision = 15, scale = 2, nullable = false)
    private BigDecimal pricePerDay;

    @Column(name = "estimated_income", precision = 15, scale = 2)
    private BigDecimal estimatedIncome;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ========== STEP 2: Thông tin chi tiết ==========

    // Vị trí
    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "ward", length = 100)
    private String ward;

    @Column(name = "latitude", length = 50)
    private String latitude;

    @Column(name = "longitude", length = 50)
    private String longitude;

    // Kỹ thuật
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "car_condition", length = 50)
    private String condition;

    @Column(name = "engine_capacity")
    private Integer engineCapacity;

    // Tiện nghi
    @Column(name = "has_air_conditioner")
    private Boolean hasAirConditioner;

    @Column(name = "has_dash_cam")
    private Boolean hasDashCam;

    @Column(name = "has_reverse_camera")
    private Boolean hasReverseCamera;

    @Column(name = "has_gps")
    private Boolean hasGPS;

    @Column(name = "has_usb")
    private Boolean hasUSB;

    @Column(name = "has_bluetooth")
    private Boolean hasBluetooth;

    @Column(name = "has_maps")
    private Boolean hasMaps;

    @Column(name = "has_360_camera")
    private Boolean has360Camera;

    @Column(name = "has_spare_wheel")
    private Boolean hasSpareWheel;

    @Column(name = "has_dvd_player")
    private Boolean hasDVDPlayer;

    @Column(name = "has_etc")
    private Boolean hasETC;

    @Column(name = "has_sunroof")
    private Boolean hasSunroof;

    // Hình ảnh
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    // Quy định
    @Column(name = "daily_km_limit")
    private Integer dailyKmLimit;

    @Column(name = "over_limit_fee", precision = 10, scale = 2)
    private BigDecimal overLimitFee;

    @Column(name = "fuel_policy", length = 100)
    private String fuelPolicy;

    @Column(name = "cancellation_policy", length = 100)
    private String cancellationPolicy;

    @Column(name = "delivery_time", length = 100)
    private String deliveryTime;

    @Column(name = "delivery_location", length = 200)
    private String deliveryLocation;

    // Bổ sung
    @Column(name = "special_notes", length = 1000)
    private String specialNotes;

    @Column(name = "renter_requirements", length = 500)
    private String renterRequirements;

    @Column(name = "cleaning_policy", length = 200)
    private String cleaningPolicy;

    // ========== Common Fields ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CarStatus status = CarStatus.DRAFT;

    @Column(name = "average_rating", precision = 2, scale = 1)
    private BigDecimal averageRating;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== ENUMS ==========
    public enum FuelType {
        PETROL("Petrol"),
        ELECTRIC("Electric"),
        DIESEL("Diesel"),
        HYBRID("Hybrid");

        private final String displayName;

        FuelType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum CarStatus {
        DRAFT("Draft"),
        PENDING("Pending"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        AVAILABLE("Available"),
        BOOKED("Booked"),
        RENTED("Rented"),
        DISABLED("Disabled"),
        MAINTENANCE("Maintenance"),
        INACTIVE("Inactive");

        private final String displayName;

        CarStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}




