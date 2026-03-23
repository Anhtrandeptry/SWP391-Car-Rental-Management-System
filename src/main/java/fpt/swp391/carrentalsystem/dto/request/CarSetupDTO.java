package fpt.swp391.carrentalsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarSetupDTO {

    // Thông tin từ màn Income Estimator
    @NotNull(message = "ID hãng xe không được để trống")
    private Long brandId;

    @NotNull(message = "ID mẫu xe không được để trống")
    private Long modelId;

    @NotNull(message = "Năm sản xuất không được để trống")
    private Integer year;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    // Thông tin thiết lập từ Step 1
    @NotBlank(message = "Biển số xe không được để trống")
    @Pattern(regexp = "^[0-9]{2}[A-Z]-[0-9]{4,5}$",
            message = "Biển số xe không đúng định dạng (VD: 30A-12345)")
    private String licensePlate;

    @NotBlank(message = "Màu sắc không được để trống")
    @Size(max = 50, message = "Màu sắc không được vượt quá 50 ký tự")
    private String color;

    @NotBlank(message = "Loại hộp số không được để trống")
    @Size(max = 50, message = "Loại hộp số không được vượt quá 50 ký tự")
    private String transmissionType;

    @NotBlank(message = "Loại nhiên liệu không được để trống")
    @Size(max = 50, message = "Loại nhiên liệu không được vượt quá 50 ký tự")
    private String fuelType;

    @NotNull(message = "Số chỗ ngồi không được để trống")
    @Min(value = 2, message = "Số chỗ ngồi phải từ 2 trở lên")
    @Max(value = 50, message = "Số chỗ ngồi không được vượt quá 50")
    private Integer seats;

    @NotNull(message = "Giá thuê không được để trống")
    @DecimalMin(value = "100000", message = "Giá thuê phải từ 100,000 VNĐ trở lên")
    private BigDecimal pricePerDay;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;

    // Thông tin bổ sung
    private BigDecimal estimatedIncome;
}

