package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BookingHistoryResponse {
    private Integer bookingId;
    private String bookingCode;
    private String carName;
    private String licensePlate;
    private String carImage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private long durationDays;
    private String pickupLocation;
    private String ownerName;
    private String ownerPhone;
    private BigDecimal totalAmount;
    private BigDecimal pricePerDay;
    private String status;

    private Double rating;
    private String comment;
}