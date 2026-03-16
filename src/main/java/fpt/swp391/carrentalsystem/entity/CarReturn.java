package fpt.swp391.carrentalsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Integer returnId;

    // Mối quan hệ One-to-One với Booking
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "actual_return_date", nullable = false)
    private LocalDateTime actualReturnDate;

    @Column(name = "odometer_reading")
    private Integer odometerReading;

    @Column(name = "damage_detected")
    private Boolean damageDetected = false;

    @Column(name = "damage_description", columnDefinition = "TEXT")
    private String damageDescription;

    @Column(name = "penalty_amount", precision = 15, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(name = "owner_confirmation")
    private Boolean ownerConfirmation = false;

    // Trường bổ sung để lưu thông tin vệ sinh (như trong giao diện bạn cung cấp trước đó)
    @Column(name = "cleaning_status", length = 100)
    private String cleaningStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.penaltyAmount == null) this.penaltyAmount = BigDecimal.ZERO;
        if (this.damageDetected == null) this.damageDetected = false;
        if (this.ownerConfirmation == null) this.ownerConfirmation = false;
    }
}