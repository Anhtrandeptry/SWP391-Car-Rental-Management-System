package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    /**
     * Find booking by PayOS orderCode for webhook processing
     * IMPORTANT: orderCode is NOT the same as bookingId
     */
    Optional<Booking> findByOrderCode(Long orderCode);


    List<Booking> findByCar_Owner_IdAndStatus(Integer ownerId, BookingStatus status);
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.car.carId = :carId " +
           "AND b.status = :status " +
           "AND ((b.startDate < :endDate AND b.endDate > :startDate))")
    long countOverlappingBookings(@Param("carId") Integer carId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   @Param("status") BookingStatus status);

    // Find bookings by customer (rental history for customer)
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Find bookings by car owner (rental history for owner)
    @Query("SELECT b FROM Booking b WHERE b.car.owner.id = :ownerId ORDER BY b.createdAt DESC")
    List<Booking> findByCarOwnerIdOrderByCreatedAtDesc(@Param("ownerId") Long ownerId);

    List<Booking> findByCustomerAndStatusIn(User customer, List<BookingStatus> statuses);
    // Find expired payment pending bookings
    @Query("SELECT b FROM Booking b WHERE b.status = :status " +
           "AND b.paymentStatus = :paymentStatus " +
           "AND b.paymentDeadline < :now")
    List<Booking> findExpiredPaymentPendingBookings(
            @Param("status") BookingStatus status,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("now") LocalDateTime now);

    // Find completed bookings for revenue calculation (non-cancelled)
    @Query("SELECT b FROM Booking b WHERE b.car.carId = :carId " +
           "AND b.status IN ('CONFIRMED', 'COMPLETED', 'IN_USE') " +
           "AND b.paymentStatus = 'PAID'")
    List<Booking> findCompletedBookingsByCarId(@Param("carId") Integer carId);

    // Calculate total revenue for a car
    @Query("SELECT COALESCE(SUM(b.rentalFee), 0) FROM Booking b WHERE b.car.carId = :carId " +
           "AND b.status IN ('CONFIRMED', 'COMPLETED', 'IN_USE') " +
           "AND b.paymentStatus = 'PAID'")
    BigDecimal calculateRevenueByCarId(@Param("carId") Integer carId);

    // Calculate total revenue for owner
    @Query("SELECT COALESCE(SUM(b.rentalFee), 0) FROM Booking b WHERE b.car.owner.id = :ownerId " +
           "AND b.status IN ('CONFIRMED', 'COMPLETED', 'IN_USE') " +
           "AND b.paymentStatus = 'PAID'")
    BigDecimal calculateTotalRevenueByOwnerId(@Param("ownerId") Long ownerId);

    // Find bookings by car
    List<Booking> findByCarCarIdOrderByCreatedAtDesc(Integer carId);

    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId ORDER BY b.createdAt DESC")
    List<Booking> findAllByCustomerId(@Param("customerId") Long customerId);


    Optional<Booking> findWithDetailsByBookingId(Integer bookingId);

    // ===== NEW: Optimized queries with JOIN FETCH to avoid N+1 problem =====

    /**
     * Get customer booking history with car and owner info (JOIN FETCH)
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.car c " +
           "JOIN FETCH c.owner " +
           "WHERE b.customer.id = :customerId " +
           "ORDER BY b.createdAt DESC")
    List<Booking> findCustomerBookingsWithDetails(@Param("customerId") Long customerId);

    /**
     * Get owner booking history with car and customer info (JOIN FETCH)
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.car c " +
           "JOIN FETCH b.customer " +
           "WHERE c.owner.id = :ownerId " +
           "ORDER BY b.createdAt DESC")
    List<Booking> findOwnerBookingsWithDetails(@Param("ownerId") Long ownerId);

    /**
     * Get booking detail with all related entities
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.car c " +
           "JOIN FETCH c.owner " +
           "JOIN FETCH b.customer " +
           "WHERE b.bookingId = :bookingId")
    Optional<Booking> findBookingWithAllDetails(@Param("bookingId") Integer bookingId);

    /**
     * Check if car has any active booking (IN_USE status)
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
           "WHERE b.car.carId = :carId " +
           "AND b.status = fpt.swp391.carrentalsystem.enums.BookingStatus.IN_USE")
    boolean hasCarInUseBooking(@Param("carId") Integer carId);

    // ===== Admin Dashboard queries =====

    /**
     * Count bookings by status
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") BookingStatus status);

    /**
     * Count bookings created after a specific date (for "new this month" stats)
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate")
    Long countBookingsCreatedAfter(@Param("startDate") LocalDateTime startDate);

    /**
     * Calculate total revenue (sum of rental fees from paid and completed/confirmed bookings)
     */
    @Query("SELECT COALESCE(SUM(b.rentalFee), 0) FROM Booking b " +
           "WHERE b.paymentStatus = fpt.swp391.carrentalsystem.enums.PaymentStatus.PAID " +
           "AND b.status <> fpt.swp391.carrentalsystem.enums.BookingStatus.CANCELLED")
    BigDecimal calculateTotalRevenue();

    /**
     * Calculate revenue for a specific period
     */
    @Query("SELECT COALESCE(SUM(b.rentalFee), 0) FROM Booking b " +
           "WHERE b.paymentStatus = fpt.swp391.carrentalsystem.enums.PaymentStatus.PAID " +
           "AND b.status <> fpt.swp391.carrentalsystem.enums.BookingStatus.CANCELLED " +
           "AND b.createdAt >= :startDate AND b.createdAt < :endDate")
    BigDecimal calculateRevenueForPeriod(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    /**
     * Get recent bookings with details for admin dashboard
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.car c " +
           "JOIN FETCH b.customer " +
           "ORDER BY b.createdAt DESC")
    List<Booking> findRecentBookingsWithDetails(org.springframework.data.domain.Pageable pageable);
    @Query("""
        SELECT 
            b.customer.id as customerId,
            COUNT(b) as totalBookings,
            COALESCE(SUM(b.totalAmount), 0) as totalSpent
        FROM Booking b
        WHERE b.status = 'COMPLETED'
        GROUP BY b.customer.id
    """)
    List<Object[]> getCustomerBookingStats();
    @Query("""
        SELECT b.car.owner.id, COUNT(DISTINCT b.car.carId)
        FROM Booking b
        WHERE b.status = 'COMPLETED'
        GROUP BY b.car.owner.id
    """)
    List<Object[]> countRentedCarsByOwner();
    @Query("""
        SELECT b.car.owner.id, COALESCE(SUM(b.totalAmount), 0)
        FROM Booking b
        WHERE b.status = 'COMPLETED'
        GROUP BY b.car.owner.id
    """)
    List<Object[]> getRevenueByOwner();

    @Query("""
    SELECT b
    FROM Booking b
    WHERE b.customer.id = :customerId
    ORDER BY b.createdAt DESC
    """)
    List<Booking> findTop5ByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("""
    SELECT b FROM Booking b 
    JOIN FETCH b.car c 
    JOIN FETCH b.customer 
    WHERE c.owner.id = :ownerId 
    ORDER BY b.createdAt DESC
    """)
    List<Booking> findTopOwnerBookingsWithDetails(
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );

    @Query("""
    SELECT COUNT(b)
    FROM Booking b
    WHERE b.car.owner.id = :ownerId
    """)
    Long countBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("""
    SELECT COALESCE(SUM(b.totalAmount), 0)
    FROM Booking b
    WHERE b.car.owner.id = :ownerId
    AND b.status IN ('CONFIRMED', 'COMPLETED', 'IN_USE')
    AND b.paymentStatus = 'PAID'
    """)
    BigDecimal sumRevenueByOwner(@Param("ownerId") Long ownerId);
}
