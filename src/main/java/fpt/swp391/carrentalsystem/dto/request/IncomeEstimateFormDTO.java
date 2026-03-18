package fpt.swp391.carrentalsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Form-backing DTO cho trang ước tính thu nhập (SSR/MVC).
 * Backend validate tất cả fields khi form submit.
 * External API data (brand, model, city) vẫn được fetch bởi JS,
 * nhưng backend PHẢI validate chúng không null/empty.
 *
 * UPDATED: pricePerDay is now manually entered by user (not auto-calculated)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeEstimateFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Vui lòng chọn hãng xe")
    private String brandId;

    @NotBlank(message = "Vui lòng chọn hãng xe")
    private String brandName;

    @NotBlank(message = "Vui lòng chọn mẫu xe")
    private String modelId;

    @NotBlank(message = "Vui lòng chọn mẫu xe")
    private String modelName;

    @NotNull(message = "Vui lòng chọn năm sản xuất")
    @Min(value = 1990, message = "Năm sản xuất phải từ 1990 trở lên")
    private Integer year;

    @NotBlank(message = "Vui lòng chọn tỉnh/thành phố")
    private String city;

    /**
     * Giá thuê mỗi ngày - do người dùng nhập thủ công
     * (Không còn tự động tính toán)
     */
    @NotNull(message = "Vui lòng nhập giá thuê mỗi ngày")
    @DecimalMin(value = "100000", message = "Giá thuê phải từ 100,000 VNĐ trở lên")
    private BigDecimal pricePerDay;

    /**
     * Chuyển đổi sang IncomeEstimateRequestDTO để gọi service
     * (giữ lại method để tương thích, nhưng không còn dùng cho tính toán)
     */
    public IncomeEstimateRequestDTO toRequestDTO() {
        return new IncomeEstimateRequestDTO(
                this.brandId,
                this.brandName,
                this.modelId,
                this.modelName,
                this.year,
                this.city
        );
    }
}
