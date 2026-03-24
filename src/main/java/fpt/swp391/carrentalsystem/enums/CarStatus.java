package fpt.swp391.carrentalsystem.enums;

public enum CarStatus {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    REJECTED("Từ chối"),
    AVAILABLE("Sẵn sàng"),
    RESERVED("Đang giữ chỗ"),
    BOOKED("Đã đặt"),
    RENTED("Đang cho thuê"),
    UNAVAILABLE("Không khả dụng"),
    DISABLED("Đã vô hiệu"),
    INACTIVE("Đã xóa");

    private final String displayName;

    CarStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

