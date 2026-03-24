package fpt.swp391.carrentalsystem.entity;

import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.FuelType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cars")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Integer carId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Relationship with CarImage
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CarImage> images = new java.util.ArrayList<>();

    @Column(name = "name", nullable = false, length = 100)
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

    @Column(name = "price_per_day", nullable = false, precision = 15, scale = 2)
    private BigDecimal pricePerDay;

    @Column(name = "estimated_income", precision = 15, scale = 2)
    private BigDecimal estimatedIncome;



    // ========== STEP 2: Thông tin chi tiết ==========

    // Vị trí
    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "province", length = 100)
    private String province;

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
    /**
     * Trạng thái của xe - sử dụng enum CarStatus từ package enums
     * @see fpt.swp391.carrentalsystem.enums.CarStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status = CarStatus.PENDING;

    @Column(name = "average_rating", precision = 2, scale = 1)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "reservation_expire_time")
    private LocalDateTime reservationExpireTime;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Convenience method for template compatibility
    public Integer getId() {
        return carId;
    }

    // Convenience method for backward compatibility
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }
}

