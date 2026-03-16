package fpt.swp391.carrentalsystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarReturnDto {

    private Integer returnId;

    // mapping từ Booking entity
    private Integer bookingId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime actualReturnDate;

    private Integer odometerReading;

    private Boolean damageDetected;

    private String damageDescription;

    private BigDecimal penaltyAmount;

    private Boolean ownerConfirmation;

    private String cleaningStatus;

    private LocalDateTime createdAt;
}