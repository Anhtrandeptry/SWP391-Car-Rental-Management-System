package fpt.swp391.carrentalsystem.dto.response;
import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingConfirmationDto {
    private Integer bookingId;
    private Integer carId;
    private String carName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String pickupLocation;
    private BigDecimal rentalFee;
    private BigDecimal depositAmount;
    private BigDecimal holdingFee;
    private BigDecimal totalAmount;
    private LocalDateTime paymentDeadline;
    private String status;
}
