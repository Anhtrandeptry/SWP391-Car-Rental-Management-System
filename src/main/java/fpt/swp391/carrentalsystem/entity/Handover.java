package fpt.swp391.carrentalsystem.entity;

import fpt.swp391.carrentalsystem.enums.HandoverStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "handover")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Handover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "booking_id", unique = true)
    private Booking booking;

    @Column(name = "fuel_level")
    private Integer fuelLevel;

    @Column(name = "odometer_reading")
    private Integer odometerReading;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private HandoverStatus status;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "handover", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HandoverImage> images;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}