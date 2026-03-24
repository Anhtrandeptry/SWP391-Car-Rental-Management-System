package fpt.swp391.carrentalsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardStatsDto {

    // User statistics
    private Long totalUsers;
    private Long totalCustomers;
    private Long totalOwners;
    private Long totalAdmins;
    private Long newUsersThisMonth;
    private Long pendingUsers;

    // Car statistics
    private Long totalCars;
    private Long availableCars;
    private Long bookedCars;
    private Long unavailableCars;
    private Long pendingCars;
    private Long newCarsThisMonth;

    // Booking statistics
    private Long totalBookings;
    private Long pendingBookings;
    private Long confirmedBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private Long inUseBookings;
    private Long newBookingsThisMonth;

    // Revenue statistics
    private BigDecimal totalRevenue;
    private BigDecimal revenueThisMonth;
    private BigDecimal revenueLastMonth;
    private Double revenueGrowthPercent;

    // Pending actions
    private Long pendingReports;
    private Long pendingCarApprovals;

    // Recent activities
    private List<RecentActivityDto> recentActivities;
    private List<RecentBookingDto> recentBookings;

    // Car status breakdown (for charts)
    private Map<String, Long> carStatusBreakdown;

    // Booking status breakdown (for charts)
    private Map<String, Long> bookingStatusBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentActivityDto {
        private String type; // USER_REGISTER, BOOKING_CREATE, REPORT_SUBMIT, CAR_REGISTER
        private String title;
        private String description;
        private String timestamp;
        private String iconClass;
        private String iconBgClass;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentBookingDto {
        private Integer bookingId;
        private String customerName;
        private String carName;
        private String status;
        private String paymentStatus;
        private BigDecimal totalAmount;
        private String createdAt;
    }
}

