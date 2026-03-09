package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalHistoryDto {
    private Integer bookingId;
    private Integer carId;
    private String carName;
    private String carBrand;
    private String carModel;
    private String licensePlate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String pickupLocation;
    private BigDecimal rentalFee;
    private BigDecimal depositAmount;
    private BigDecimal holdingFee;
    private BigDecimal totalAmount;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;

    // Owner info (for customer view)
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;

    // Customer info (for owner view)
    private String customerName;
    private String customerPhone;
    private String customerEmail;
}

