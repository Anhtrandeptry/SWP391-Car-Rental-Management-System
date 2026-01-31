package fpt.swp391.carrentalsystem.dto.request;



import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarInfoDTO {

    // ========== Thông tin từ Step 1 (readonly) ==========
    private Long brandId;
    private Long modelId;
    private String brandName;
    private String modelName;
    private Integer year;
    private String city;
    private String licensePlate;
    private String color;
    private String transmissionType;
    private String fuelType;
    private Integer seats;
    private BigDecimal pricePerDay;
    private String description;

    // ========== Thông tin vị trí ==========
    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 200, message = "Địa chỉ không được vượt quá 200 ký tự")
    private String address;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 100, message = "Quận/Huyện không được vượt quá 100 ký tự")
    private String district;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    private String ward;

    @Size(max = 50, message = "Vĩ độ không hợp lệ")
    private String latitude;

    @Size(max = 50, message = "Kinh độ không hợp lệ")
    private String longitude;

    // ========== Thông tin kỹ thuật ==========
    @NotNull(message = "Số km đã đi không được để trống")
    @Min(value = 0, message = "Số km phải lớn hơn hoặc bằng 0")
    @Max(value = 1000000, message = "Số km không hợp lệ")
    private Integer mileage;

    @NotBlank(message = "Tình trạng xe không được để trống")
    @Pattern(regexp = "Mới|Rất tốt|Tốt|Trung bình|Cũ",
            message = "Tình trạng xe không hợp lệ")
    private String condition;

    @DecimalMin(value = "0.0", message = "Năng lượng tiêu hao phải lớn hơn 0")
    @DecimalMax(value = "50.0", message = "Năng lượng tiêu hao không hợp lệ")
    private BigDecimal fuelConsumption;

    @Min(value = 500, message = "Dung tích động cơ tối thiểu là 500cc")
    @Max(value = 10000, message = "Dung tích động cơ tối đa là 10,000cc")
    private Integer engineCapacity;

    // ========== Tiện nghi ==========
    private Boolean hasAirConditioner;
    private Boolean hasDashCam;
    private Boolean hasReverseCamera;
    private Boolean hasGPS;
    private Boolean hasUSB;
    private Boolean hasBluetooth;
    private Boolean hasMaps;
    private Boolean has360Camera;
    private Boolean hasSpareWheel;
    private Boolean hasDVDPlayer;
    private Boolean hasETC;
    private Boolean hasSunroof;

    // ========== Hình ảnh ==========
    @Size(max = 500, message = "URL ảnh bìa quá dài")
    private String coverImage;

    @Size(max = 2000, message = "Danh sách ảnh quá dài")
    private String images;

    // ========== Quy định thuê xe ==========
    @NotNull(message = "Giới hạn km/ngày không được để trống")
    @Min(value = 50, message = "Giới hạn km tối thiểu là 50km/ngày")
    @Max(value = 1000, message = "Giới hạn km tối đa là 1000km/ngày")
    private Integer dailyKmLimit;

    @NotNull(message = "Phụ phí vượt km không được để trống")
    @DecimalMin(value = "1000", message = "Phụ phí vượt km tối thiểu 1,000 VNĐ/km")
    private BigDecimal overLimitFee;

    @NotBlank(message = "Chính sách nhiên liệu không được để trống")
    @Size(max = 100)
    private String fuelPolicy;

    @Size(max = 100)
    private String cancellationPolicy;

    @NotBlank(message = "Thời gian giao xe không được để trống")
    @Size(max = 100)
    private String deliveryTime;

    @NotBlank(message = "Địa điểm giao xe không được để trống")
    @Size(max = 200)
    private String deliveryLocation;

    // ========== Thông tin bổ sung ==========
    @Size(max = 1000, message = "Ghi chú đặc biệt không được vượt quá 1000 ký tự")
    private String specialNotes;

    @Size(max = 500, message = "Yêu cầu với người thuê không được vượt quá 500 ký tự")
    private String renterRequirements;

    @Size(max = 200, message = "Chính sách vệ sinh không được vượt quá 200 ký tự")
    private String cleaningPolicy;
}
