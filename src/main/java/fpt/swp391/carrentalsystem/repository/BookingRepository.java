package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.PaymentStatus;
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
           "AND b.status IN ('CONFIRMED', 'COMPLETED') " +
           "AND b.paymentStatus = 'PAID'")
    List<Booking> findCompletedBookingsByCarId(@Param("carId") Integer carId);

    // Calculate total revenue for a car
    @Query("SELECT COALESCE(SUM(b.rentalFee), 0) FROM Booking b WHERE b.car.carId = :carId " +
           "AND b.status IN ('CONFIRMED', 'COMPLETED') " +
           "AND b.paymentStatus = 'PAID'")
    BigDecimal calculateRevenueByCarId(@Param("carId") Integer carId);

    // Calculate total revenue for owner
    @Query("SELECT COALESCE(SUM(b.rentalFee), 0) FROM Booking b WHERE b.car.owner.id = :ownerId " +
           "AND b.status IN ('CONFIRMED', 'COMPLETED') " +
           "AND b.paymentStatus = 'PAID'")
    BigDecimal calculateTotalRevenueByOwnerId(@Param("ownerId") Long ownerId);

    // Find bookings by car
    List<Booking> findByCarCarIdOrderByCreatedAtDesc(Integer carId);

    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId ORDER BY b.createdAt DESC")
    List<Booking> findAllByCustomerId(@Param("customerId") Long customerId);


    Optional<Booking> findWithDetailsByBookingId(Integer bookingId);
}
