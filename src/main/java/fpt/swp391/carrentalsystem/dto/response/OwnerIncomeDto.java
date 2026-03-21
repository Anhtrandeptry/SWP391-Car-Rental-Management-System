package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerIncomeDto {
    private Long ownerId;
    private String ownerName;
    private BigDecimal totalIncome;
    private Integer totalCars;
    private Integer totalCompletedBookings;
    private List<CarIncomeDto> carIncomes;
}

