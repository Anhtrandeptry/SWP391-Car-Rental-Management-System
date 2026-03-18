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

    @NotBlank(message = "ID hãng xe không được để trống")
    private String brandId;  // make_id từ CarQuery API

    @NotBlank(message = "Tên hãng xe không được để trống")
    private String brandName;  // make_display từ CarQuery API

    @NotBlank(message = "ID mẫu xe không được để trống")
    private String modelId;  // model_name từ CarQuery API

    @NotBlank(message = "Tên mẫu xe không được để trống")
    private String modelName;  // model_name từ CarQuery API

    @NotNull(message = "Năm sản xuất không được để trống")
    private Integer year;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;
}


