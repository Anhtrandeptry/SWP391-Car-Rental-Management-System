package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarIncomeDto {
    private Integer carId;
    private String carName;
    private String licensePlate;
    private String status;
    private BigDecimal totalIncome;
    private Integer totalBookings;
    private Integer completedBookings;
    private List<BookingIncomeDto> bookings;
}

