package fpt.swp391.carrentalsystem.service.admin;

import fpt.swp391.carrentalsystem.dto.response.AdminDashboardStatsDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public AdminDashboardStatsDto getDashboardStats() {
        log.info("Fetching admin dashboard statistics");

        // Get start of current month
        LocalDateTime startOfMonth = LocalDateTime.now()
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Get start of last month
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);

        // User statistics
        Long totalUsers = countTotalUsers();
        Long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        Long totalOwners = userRepository.countByRole(Role.CAR_OWNER);
        Long totalAdmins = userRepository.countByRole(Role.ADMIN);
        Long newUsersThisMonth = userRepository.countUsersCreatedAfter(startOfMonth);
        Long pendingUsers = userRepository.countByStatus(UserStatus.PENDING);

        // Car statistics
        Map<String, Long> carStatusBreakdown = countCarsByStatus();
        Long totalCars = carRepository.count();
        Long availableCars = carStatusBreakdown.getOrDefault("AVAILABLE", 0L);
        Long bookedCars = carStatusBreakdown.getOrDefault("BOOKED", 0L);
        Long unavailableCars = carStatusBreakdown.getOrDefault("UNAVAILABLE", 0L);
        Long pendingCars = carStatusBreakdown.getOrDefault("PENDING", 0L);
        Long newCarsThisMonth = carRepository.countCarsCreatedAfter(startOfMonth);

        // Booking statistics
        Map<String, Long> bookingStatusBreakdown = countBookingsByStatus();
        Long totalBookings = countTotalBookings();
        Long pendingBookings = bookingStatusBreakdown.getOrDefault("PENDING", 0L)
                             + bookingStatusBreakdown.getOrDefault("PAYMENT_PENDING", 0L);
        Long confirmedBookings = bookingStatusBreakdown.getOrDefault("CONFIRMED", 0L);
        Long completedBookings = bookingStatusBreakdown.getOrDefault("COMPLETED", 0L);
        Long cancelledBookings = bookingStatusBreakdown.getOrDefault("CANCELLED", 0L);
        Long inUseBookings = bookingStatusBreakdown.getOrDefault("IN_USE", 0L);
        Long newBookingsThisMonth = bookingRepository.countBookingsCreatedAfter(startOfMonth);

        // Revenue statistics
        BigDecimal totalRevenue = calculateTotalRevenue();
        BigDecimal revenueThisMonth = bookingRepository.calculateRevenueForPeriod(startOfMonth, LocalDateTime.now());
        BigDecimal revenueLastMonth = bookingRepository.calculateRevenueForPeriod(startOfLastMonth, startOfMonth);

        Double revenueGrowthPercent = 0.0;
        if (revenueLastMonth != null && revenueLastMonth.compareTo(BigDecimal.ZERO) > 0) {
            revenueGrowthPercent = revenueThisMonth.subtract(revenueLastMonth)
                    .divide(revenueLastMonth, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Pending actions
        Long pendingReports = 0L; // TODO: Add ReportRepository if exists
        Long pendingCarApprovals = carRepository.countPendingApprovals();

        // Recent data
        List<AdminDashboardStatsDto.RecentBookingDto> recentBookings = getRecentBookings(5);
        List<AdminDashboardStatsDto.RecentActivityDto> recentActivities = getRecentActivities(5);

        return AdminDashboardStatsDto.builder()
                // User stats
                .totalUsers(totalUsers)
                .totalCustomers(totalCustomers)
                .totalOwners(totalOwners)
                .totalAdmins(totalAdmins)
                .newUsersThisMonth(newUsersThisMonth)
                .pendingUsers(pendingUsers)
                // Car stats
                .totalCars(totalCars)
                .availableCars(availableCars)
                .bookedCars(bookedCars)
                .unavailableCars(unavailableCars)
                .pendingCars(pendingCars)
                .newCarsThisMonth(newCarsThisMonth)
                // Booking stats
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .confirmedBookings(confirmedBookings)
                .completedBookings(completedBookings)
                .cancelledBookings(cancelledBookings)
                .inUseBookings(inUseBookings)
                .newBookingsThisMonth(newBookingsThisMonth)
                // Revenue stats
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .revenueThisMonth(revenueThisMonth != null ? revenueThisMonth : BigDecimal.ZERO)
                .revenueLastMonth(revenueLastMonth != null ? revenueLastMonth : BigDecimal.ZERO)
                .revenueGrowthPercent(revenueGrowthPercent)
                // Pending actions
                .pendingReports(pendingReports)
                .pendingCarApprovals(pendingCarApprovals)
                // Recent data
                .recentBookings(recentBookings)
                .recentActivities(recentActivities)
                // Breakdowns
                .carStatusBreakdown(carStatusBreakdown)
                .bookingStatusBreakdown(bookingStatusBreakdown)
                .build();
    }

    @Override
    public Long countTotalUsers() {
        return userRepository.count();
    }

    @Override
    public Long countTotalBookings() {
        return bookingRepository.count();
    }

    @Override
    public BigDecimal calculateTotalRevenue() {
        BigDecimal revenue = bookingRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public Map<String, Long> countCarsByStatus() {
        Map<String, Long> result = new HashMap<>();
        for (CarStatus status : CarStatus.values()) {
            Long count = carRepository.countByStatus(status);
            result.put(status.name(), count != null ? count : 0L);
        }
        return result;
    }

    private Map<String, Long> countBookingsByStatus() {
        Map<String, Long> result = new HashMap<>();
        for (BookingStatus status : BookingStatus.values()) {
            Long count = bookingRepository.countByStatus(status);
            result.put(status.name(), count != null ? count : 0L);
        }
        return result;
    }

    @Override
    public List<AdminDashboardStatsDto.RecentBookingDto> getRecentBookings(int limit) {
        try {
            List<Booking> bookings = bookingRepository.findRecentBookingsWithDetails(PageRequest.of(0, limit));

            return bookings.stream()
                    .map(b -> AdminDashboardStatsDto.RecentBookingDto.builder()
                            .bookingId(b.getBookingId())
                            .customerName(b.getCustomer().getFirstName() + " " + b.getCustomer().getLastName())
                            .carName(b.getCar().getName())
                            .status(b.getStatus().name())
                            .paymentStatus(b.getPaymentStatus().name())
                            .totalAmount(b.getTotalAmount())
                            .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().format(DATE_FORMATTER) : "N/A")
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching recent bookings: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<AdminDashboardStatsDto.RecentActivityDto> getRecentActivities(int limit) {
        List<AdminDashboardStatsDto.RecentActivityDto> activities = new ArrayList<>();

        try {
            // Get recent bookings as activities
            List<Booking> recentBookings = bookingRepository.findRecentBookingsWithDetails(PageRequest.of(0, limit));

            for (Booking booking : recentBookings) {
                String iconClass;
                String iconBgClass;
                String title;

                switch (booking.getStatus()) {
                    case CONFIRMED:
                        iconClass = "bi bi-check-all";
                        iconBgClass = "background: #dcfce7; color: #16a34a;";
                        title = "Đơn đặt xe xác nhận";
                        break;
                    case CANCELLED:
                        iconClass = "bi bi-x-circle";
                        iconBgClass = "background: #fce7f3; color: #db2777;";
                        title = "Đơn đặt xe bị hủy";
                        break;
                    case PAYMENT_PENDING:
                        iconClass = "bi bi-hourglass-split";
                        iconBgClass = "background: #fef3c7; color: #d97706;";
                        title = "Đơn đặt xe chờ thanh toán";
                        break;
                    default:
                        iconClass = "bi bi-calendar-plus";
                        iconBgClass = "background: #e0f2fe; color: #0891b2;";
                        title = "Đơn đặt xe mới #" + booking.getBookingId();
                }

                activities.add(AdminDashboardStatsDto.RecentActivityDto.builder()
                        .type("BOOKING")
                        .title(title)
                        .description(formatTimeAgo(booking.getCreatedAt()) + " • " + booking.getCar().getName())
                        .timestamp(booking.getCreatedAt() != null ? booking.getCreatedAt().format(DATE_FORMATTER) : "N/A")
                        .iconClass(iconClass)
                        .iconBgClass(iconBgClass)
                        .build());
            }
        } catch (Exception e) {
            log.error("Error fetching recent activities: {}", e.getMessage());
        }

        return activities;
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (minutes < 1) return "Vừa xong";
        if (minutes < 60) return minutes + " phút trước";

        long hours = minutes / 60;
        if (hours < 24) return hours + " giờ trước";

        long days = hours / 24;
        if (days < 7) return days + " ngày trước";

        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}

