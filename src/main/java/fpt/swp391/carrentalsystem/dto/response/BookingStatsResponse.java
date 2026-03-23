package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingStatsResponse {
    // === Các chỉ số chính (Top Cards) ===
    private Long totalBookings;
    private Long completedCount;
    private Double completionRate; // Tỷ lệ hoàn thành (82.3%)
    private Double cancellationRate; // Tỷ lệ hủy (6.4%)

    // === Thống kê theo trạng thái (Ảnh 2 - các hàng màu sắc) ===
    private Integer statusCompleted;
    private Integer statusConfirmed;
    private Integer statusInProgress;
    private Integer statusPending;
    private Integer statusCancelled;

    // === Dữ liệu Biểu đồ (Charts) ===
    // Phân bổ Booking (Biểu đồ tròn)
    private Map<String, Double> bookingStatusDistribution;

    // Xu hướng Booking theo tháng (Biểu đồ cột so sánh)
    private List<String> months;              // ["Jan", "Feb", ...]
    private List<Integer> totalBookingsByMonth; // Cột xanh nhạt
    private List<Integer> completedByMonth;     // Cột xanh đậm
    private List<Integer> cancelledByMonth;     // Cột đỏ

    // === Chi tiết thống kê bảng (Ảnh 2 - phía dưới cùng) ===
    private List<StatusDetailDTO> statusDetails;

    @Data
    @AllArgsConstructor
    public static class StatusDetailDTO {
        private String statusName;
        private Integer quantity;
        private Double percentage;
        private Integer growthComparedToLastPeriod;
        private String trend; // "up" hoặc "down"
    }
}