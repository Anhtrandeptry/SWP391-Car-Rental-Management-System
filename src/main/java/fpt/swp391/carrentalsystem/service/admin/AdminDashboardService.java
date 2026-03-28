package fpt.swp391.carrentalsystem.service.admin;

import fpt.swp391.carrentalsystem.dto.response.AdminDashboardStatsDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AdminDashboardService {

    /**
     * Get all dashboard statistics aggregated in a single DTO
     */
    AdminDashboardStatsDto getDashboardStats();

    /**
     * Get total number of users
     */
    Long countTotalUsers();

    /**
     * Get total number of bookings
     */
    Long countTotalBookings();

    /**
     * Calculate total revenue from completed bookings
     */
    BigDecimal calculateTotalRevenue();

    /**
     * Count cars grouped by status
     */
    Map<String, Long> countCarsByStatus();

    /**
     * Get recent bookings
     */
    List<AdminDashboardStatsDto.RecentBookingDto> getRecentBookings(int limit);

    /**
     * Get recent system activities
     */
    List<AdminDashboardStatsDto.RecentActivityDto> getRecentActivities(int limit);
}
