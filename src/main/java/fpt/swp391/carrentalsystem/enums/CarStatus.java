package fpt.swp391.carrentalsystem.enums;

/**
 * Enum định nghĩa các trạng thái của xe trong hệ thống
 *
 * Extracted from Car entity to a dedicated enum class for:
 * - Better maintainability and reusability
 * - Centralized status management
 * - Type-safe status handling across the application
 *
 * @author Refactored from Car.CarStatus
 */
public enum CarStatus {

    /**
     * Xe mới tạo, đang chờ hoàn thiện thông tin (bản nháp)
     */
    DRAFT("Bản nháp", "Xe đang được tạo, chưa hoàn thiện"),

    /**
     * Xe đã gửi và đang chờ admin duyệt
     */
    PENDING("Chờ duyệt", "Xe đang chờ admin xét duyệt"),

    /**
     * Xe đã được admin duyệt và sẵn sàng cho thuê
     */
    APPROVED("Đã duyệt", "Xe đã được duyệt, chờ kích hoạt"),

    /**
     * Xe bị từ chối bởi admin
     */
    REJECTED("Bị từ chối", "Xe không đạt yêu cầu, bị từ chối"),

    /**
     * Xe sẵn sàng để cho thuê
     */
    AVAILABLE("Sẵn sàng", "Xe có thể cho thuê"),

    /**
     * Xe đã được đặt trước (chưa bắt đầu thuê)
     */
    RESERVED("Đã đặt trước", "Xe đã được đặt trước"),

    /**
     * Xe đã được đặt và đang chờ xác nhận
     */
    BOOKED("Đã đặt", "Xe đã được đặt, đang chờ xác nhận"),

    /**
     * Xe đang được thuê
     */
    RENTED("Đang thuê", "Xe đang được thuê"),

    /**
     * Xe tạm thời không khả dụng
     */
    UNAVAILABLE("Không khả dụng", "Xe tạm thời không thể cho thuê"),

    /**
     * Xe bị vô hiệu hóa (xóa mềm)
     */
    DISABLED("Vô hiệu hóa", "Xe đã bị vô hiệu hóa"),

    /**
     * Xe đang bảo trì
     */
    MAINTENANCE("Đang bảo trì", "Xe đang trong quá trình bảo dưỡng"),

    /**
     * Xe không hoạt động (đã xóa mềm)
     */
    INACTIVE("Ngưng hoạt động", "Xe đã ngưng hoạt động");

    private final String displayName;
    private final String description;

    /**
     * Constructor
     * @param displayName Tên hiển thị cho người dùng
     * @param description Mô tả chi tiết về trạng thái
     */
    CarStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Lấy tên hiển thị
     * @return Tên hiển thị thân thiện với người dùng
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Lấy mô tả
     * @return Mô tả chi tiết về trạng thái
     */
    public String getDescription() {
        return description;
    }

    /**
     * Chuyển đổi từ String sang CarStatus (null-safe)
     *
     * @param value Giá trị string cần chuyển đổi
     * @return CarStatus tương ứng hoặc null nếu không tìm thấy
     */
    public static CarStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalizedValue = value.trim().toUpperCase();

        for (CarStatus status : CarStatus.values()) {
            if (status.name().equals(normalizedValue)) {
                return status;
            }
        }

        // Fallback: thử map từ displayName (tiếng Việt)
        for (CarStatus status : CarStatus.values()) {
            if (status.displayName.equalsIgnoreCase(value.trim())) {
                return status;
            }
        }

        return null;
    }

    /**
     * Chuyển đổi từ String sang CarStatus với giá trị mặc định
     *
     * @param value Giá trị string cần chuyển đổi
     * @param defaultStatus Giá trị mặc định nếu không tìm thấy
     * @return CarStatus tương ứng hoặc defaultStatus
     */
    public static CarStatus fromStringOrDefault(String value, CarStatus defaultStatus) {
        CarStatus result = fromString(value);
        return result != null ? result : defaultStatus;
    }

    /**
     * Kiểm tra xem trạng thái có cho phép thuê xe không
     * @return true nếu xe có thể được thuê
     */
    public boolean isRentable() {
        return this == AVAILABLE;
    }

    /**
     * Kiểm tra xem trạng thái có đang trong quá trình thuê không
     * @return true nếu xe đang được đặt hoặc đang thuê
     */
    public boolean isInRentalProcess() {
        return this == RESERVED || this == BOOKED || this == RENTED;
    }

    /**
     * Kiểm tra xem xe có thể chỉnh sửa không
     * @return true nếu xe có thể chỉnh sửa thông tin
     */
    public boolean isEditable() {
        return this == DRAFT || this == PENDING || this == REJECTED ||
               this == APPROVED || this == AVAILABLE || this == UNAVAILABLE;
    }

    /**
     * Kiểm tra xem xe có thể xóa không
     * @return true nếu xe có thể xóa
     */
    public boolean isDeletable() {
        return this != BOOKED && this != RENTED && this != RESERVED;
    }

    /**
     * Kiểm tra xem xe có đang hoạt động không
     * @return true nếu xe đang hoạt động (không bị vô hiệu hóa)
     */
    public boolean isActive() {
        return this != INACTIVE && this != DISABLED;
    }
}
