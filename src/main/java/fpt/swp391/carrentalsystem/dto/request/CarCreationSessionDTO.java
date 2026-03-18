package fpt.swp391.carrentalsystem.dto.request;

import fpt.swp391.carrentalsystem.validation.CarCreationValidation;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO lưu trữ dữ liệu tạo xe trong session (multi-step form).
 * Jakarta Bean Validation được áp dụng theo validation group cho từng bước.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarCreationSessionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== INCOME ESTIMATE DATA ==========
    private String brandName;
    private String modelName;
    private Integer year;
    private String city;
    private BigDecimal estimatedMonthlyIncome;
    private BigDecimal suggestedPricePerDay;

    // ========== STEP 1: Thiết lập xe ==========
    @NotBlank(message = "Biển số xe không được để trống", groups = CarCreationValidation.Step1.class)
    @Size(max = 20, message = "Biển số xe không được quá 20 ký tự", groups = CarCreationValidation.Step1.class)
    private String licensePlate;

    @NotBlank(message = "Vui lòng chọn màu sắc", groups = CarCreationValidation.Step1.class)
    private String color;

    @NotBlank(message = "Vui lòng chọn loại hộp số", groups = CarCreationValidation.Step1.class)
    private String transmissionType;

    @NotBlank(message = "Vui lòng chọn loại nhiên liệu", groups = CarCreationValidation.Step1.class)
    private String fuelType;

    @NotNull(message = "Vui lòng chọn số chỗ ngồi", groups = CarCreationValidation.Step1.class)
    private Integer seats;

    @NotNull(message = "Vui lòng nhập giá thuê mỗi ngày", groups = CarCreationValidation.Step1.class)
    @DecimalMin(value = "100000", message = "Giá thuê phải từ 100,000 VNĐ trở lên", groups = CarCreationValidation.Step1.class)
    private BigDecimal pricePerDay;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự", groups = CarCreationValidation.Step1.class)
    private String description;

    // Flag để biết step 1 đã hoàn thành chưa
    private boolean step1Completed = false;

    // ========== STEP 2: Thông tin chi tiết ==========
    // Vị trí
    private String address;
    private String province;
    private String district;
    private String ward;

    @NotBlank(message = "Vui lòng chọn Tỉnh/Thành phố", groups = CarCreationValidation.Step2.class)
    private String provinceCode;

    @NotBlank(message = "Vui lòng chọn Quận/Huyện", groups = CarCreationValidation.Step2.class)
    private String districtCode;

    @NotBlank(message = "Vui lòng chọn Phường/Xã", groups = CarCreationValidation.Step2.class)
    private String wardCode;

    // Kỹ thuật
    @NotNull(message = "Vui lòng nhập số km đã đi", groups = CarCreationValidation.Step2.class)
    @Min(value = 0, message = "Số km đã đi phải >= 0", groups = CarCreationValidation.Step2.class)
    private Integer mileage;

    private BigDecimal fuelConsumption;

    // Tiện nghi
    private Boolean hasAirConditioner = false;
    private Boolean hasDashCam = false;
    private Boolean hasReverseCamera = false;
    private Boolean hasGPS = false;
    private Boolean hasUSB = false;
    private Boolean hasBluetooth = false;
    private Boolean hasMaps = false;
    private Boolean has360Camera = false;
    private Boolean hasSpareWheel = false;
    private Boolean hasDVDPlayer = false;
    private Boolean hasETC = false;
    private Boolean hasSunroof = false;

    // Thông tin bổ sung
    private String specialNotes;

    // Flag để biết step 2 đã hoàn thành chưa
    private boolean step2Completed = false;

    // ========== STEP 3: Giấy tờ ==========
    private List<Long> documentIds = new ArrayList<>();
    private boolean step3Completed = false;

    // ========== UTILITY METHODS ==========

    /**
     * Copy dữ liệu từ IncomeEstimateResponse vào session
     */
    public void copyFromIncomeEstimate(String brandName, String modelName,
                                        Integer year, String city,
                                        BigDecimal estimatedMonthlyIncome,
                                        BigDecimal suggestedPricePerDay) {
        this.brandName = brandName;
        this.modelName = modelName;
        this.year = year;
        this.city = city;
        this.estimatedMonthlyIncome = estimatedMonthlyIncome;
        this.suggestedPricePerDay = suggestedPricePerDay;
        this.pricePerDay = suggestedPricePerDay;
    }

    /**
     * Kiểm tra dữ liệu income estimate đã có chưa
     */
    public boolean hasIncomeEstimateData() {
        return brandName != null && modelName != null
               && year != null && city != null;
    }

    /**
     * Reset step 2 data (khi quay lại step 1)
     */
    public void resetStep2() {
        this.step1Completed = false;
        this.step2Completed = false;
    }

    /**
     * Reset step 3 data (khi quay lại step 2)
     */
    public void resetStep3() {
        this.step2Completed = false;
        this.step3Completed = false;
        this.documentIds.clear();
    }

    /**
     * Lấy danh sách tiện ích đã chọn
     */
    public List<String> getSelectedUtilities() {
        List<String> utilities = new ArrayList<>();
        if (Boolean.TRUE.equals(hasAirConditioner)) utilities.add("Điều hòa");
        if (Boolean.TRUE.equals(hasDashCam)) utilities.add("Camera hành trình");
        if (Boolean.TRUE.equals(hasReverseCamera)) utilities.add("Camera lùi");
        if (Boolean.TRUE.equals(hasGPS)) utilities.add("Định vị GPS");
        if (Boolean.TRUE.equals(hasUSB)) utilities.add("Khe cắm USB");
        if (Boolean.TRUE.equals(hasBluetooth)) utilities.add("Bluetooth");
        if (Boolean.TRUE.equals(hasMaps)) utilities.add("Bản đồ");
        if (Boolean.TRUE.equals(has360Camera)) utilities.add("Camera 360");
        if (Boolean.TRUE.equals(hasSpareWheel)) utilities.add("Lốp dự phòng");
        if (Boolean.TRUE.equals(hasDVDPlayer)) utilities.add("Đầu DVD");
        if (Boolean.TRUE.equals(hasETC)) utilities.add("Thu phí ETC");
        if (Boolean.TRUE.equals(hasSunroof)) utilities.add("Cửa sổ trời");
        return utilities;
    }
}
