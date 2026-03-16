package fpt.swp391.carrentalsystem.service.admin.impl;

import fpt.swp391.carrentalsystem.dto.response.BookingStatsResponse;
import fpt.swp391.carrentalsystem.dto.response.RevenueStatsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReportMockService {

    /**
     * Mock dữ liệu cho Revenue Report (Ảnh 1)
     */
    public RevenueStatsResponse getMockRevenueStats() {
        // 1. Tạo danh sách Top 5 xe doanh thu cao
        List<RevenueStatsResponse.TopCarDTO> top5Cars = new ArrayList<>();
        top5Cars.add(new RevenueStatsResponse.TopCarDTO("Toyota Camry 2023", 125000000.0, 35, 4.8, "camry.jpg"));
        top5Cars.add(new RevenueStatsResponse.TopCarDTO("Honda CR-V", 118000000.0, 32, 4.7, "crv.jpg"));
        top5Cars.add(new RevenueStatsResponse.TopCarDTO("Mercedes E300", 112000000.0, 28, 4.9, "mercedes.jpg"));
        top5Cars.add(new RevenueStatsResponse.TopCarDTO("BMW X5", 105000000.0, 26, 4.8, "bmw.jpg"));
        top5Cars.add(new RevenueStatsResponse.TopCarDTO("Mazda CX-5", 98000000.0, 30, 4.6, "cx5.jpg"));

        // 2. Mock dữ liệu biểu đồ đường (Xu hướng doanh thu 30 ngày)
        List<String> labelsDay = List.of("22/12", "24/12", "26/12", "28/12", "30/12", "1/1", "3/1", "5/1", "7/1", "9/1", "11/1", "13/1", "15/1", "17/1", "19/1");
        List<Double> revenueTrend = List.of(18.0, 62.0, 28.0, 68.0, 42.0, 12.0, 38.0, 15.0, 25.0, 75.0, 25.0, 72.0, 15.0, 68.0, 20.0);

        // 3. Mock biểu đồ cột 12 tháng
        List<Double> monthlyRevenue = List.of(180.0, 210.0, 195.0, 230.0, 255.0, 280.0, 310.0, 290.0, 320.0, 340.0, 315.0, 360.0);

        return RevenueStatsResponse.builder()
                .totalRevenue(2850000000.0)
                .revenueGrowthPercent(23.5)
                .totalBookings(487L)
                .bookingGrowthPercent(12.3)
                .averageRevenuePerBooking(5850000.0)
                .averageGrowthPercent(8.7)
                .topCarName("Toyota Camry 2023")
                .topCarRevenue(125000000.0)
                .topCarGrowthPercent(15.2)
                .labelsDay(labelsDay)
                .revenueTrend(revenueTrend)
                .revenueByType(Map.of("Sedan", 29.8, "SUV", 33.3, "Luxury", 25.3, "MPV", 11.6))
                .monthlyRevenue(monthlyRevenue)
                .top5Cars(top5Cars)
                .build();
    }

    /**
     * Mock dữ liệu cho Booking Statistics (Ảnh 2)
     */
    public BookingStatsResponse getMockBookingStats() {
        // 1. Dữ liệu bảng chi tiết thống kê (phía dưới cùng ảnh 2)
        List<BookingStatsResponse.StatusDetailDTO> details = new ArrayList<>();
        details.add(new BookingStatsResponse.StatusDetailDTO("Hoàn Thành", 248, 50.9, 11, "up"));
        details.add(new BookingStatsResponse.StatusDetailDTO("Đã Xác Nhận", 89, 18.3, 13, "up"));
        details.add(new BookingStatsResponse.StatusDetailDTO("Đang Diễn Ra", 67, 13.8, 7, "up"));
        details.add(new BookingStatsResponse.StatusDetailDTO("Chờ Duyệt", 52, 10.7, 10, "up"));
        details.add(new BookingStatsResponse.StatusDetailDTO("Đã Hủy", 31, 6.4, -1, "down"));

        return BookingStatsResponse.builder()
                .totalBookings(487L)
                .completedCount(248L)
                .completionRate(82.3)
                .cancellationRate(6.4)
                .statusCompleted(248)
                .statusConfirmed(89)
                .statusInProgress(67)
                .statusPending(52)
                .statusCancelled(31)
                // Biểu đồ cột so sánh theo tháng
                .months(List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"))
                .totalBookingsByMonth(List.of(45, 52, 48, 60, 65, 70, 75, 72, 78, 80, 76, 88))
                .completedByMonth(List.of(35, 46, 38, 55, 58, 62, 68, 65, 70, 72, 68, 78))
                .cancelledByMonth(List.of(2, 3, 1, 4, 2, 3, 5, 4, 3, 2, 5, 4))
                // Biểu đồ tròn phân bổ
                .bookingStatusDistribution(Map.of(
                        "Hoàn Thành", 50.9,
                        "Đã Xác Nhận", 18.3,
                        "Đang Diễn Ra", 13.8,
                        "Chờ Duyệt", 10.7,
                        "Đã Hủy", 6.4
                ))
                .statusDetails(details)
                .build();
    }
}