package fpt.swp391.carrentalsystem.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeEstimateRequestDTO {

    @NotNull(message = "ID hãng xe không được để trống")
    private Long brandId;

    @NotNull(message = "ID mẫu xe không được để trống")
    private Long modelId;

    @NotNull(message = "Năm sản xuất không được để trống")
    private Integer year;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;
}


