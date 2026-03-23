package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatsResponse {
    // === Các chỉ số tổng quan (Top Cards) ===
    private Double totalRevenue;
    private Double revenueGrowthPercent; // % tăng trưởng so với kỳ trước

    private Long totalBookings;
    private Double bookingGrowthPercent;

    private Double averageRevenuePerBooking;
    private Double averageGrowthPercent;

    // === Thông tin xe hiệu suất cao nhất ===
    private String topCarName;
    private Double topCarRevenue;
    private Double topCarGrowthPercent;

    // === Dữ liệu Biểu đồ (Charts) ===
    private List<String> labelsDay;      // Trục X: ["22/12", "24/12", ...]
    private List<Double> revenueTrend;   // Trục Y: [15.0, 35.0, 60.0, ...]

    private Map<String, Double> revenueByType; // ["Sedan": 29.8, "SUV": 33.3, ...]

    private List<Double> monthlyRevenue; // Cho biểu đồ cột 12 tháng

    // === Danh sách Top 5 Xe (Ảnh 1 - góc dưới phải) ===
    private List<TopCarDTO> top5Cars;

    @Data
    @AllArgsConstructor
    public static class TopCarDTO {
        private String name;
        private Double revenue;
        private Integer bookingCount;
        private Double rating;
        private String imageUrl;
    }
}