package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingIncomeDto {
    private Integer bookingId;
    private String customerName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal rentalFee;
    private String status;
    private LocalDateTime createdAt;
}

