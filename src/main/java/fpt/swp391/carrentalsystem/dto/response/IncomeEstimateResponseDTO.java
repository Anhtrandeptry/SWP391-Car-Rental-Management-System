package fpt.swp391.carrentalsystem.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeEstimateResponseDTO {

    private String brandName;
    private String modelName;
    private Integer year;
    private String city;
    private BigDecimal estimatedDailyIncome;
    private BigDecimal estimatedMonthlyIncome;
    private BigDecimal suggestedPricePerDay;
    private String recommendation;
}

