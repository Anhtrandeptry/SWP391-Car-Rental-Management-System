package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Integer> {

    // Find available cars for booking
    List<Car> findByStatus(CarStatus status);

    // Find cars by owner
    List<Car> findByOwner_Id(Long ownerId);

    // Find car with pessimistic lock for reservation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Car c WHERE c.carId = :carId")
    Optional<Car> findByIdWithLock(@Param("carId") Integer carId);

    // Find expired reservations
    @Query("SELECT c FROM Car c WHERE c.status = :status AND c.reservationExpireTime < :now")
    List<Car> findExpiredReservations(@Param("status") CarStatus status, @Param("now") LocalDateTime now);

    // Check if car has active booking
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.car.carId = :carId " +
           "AND b.status IN ('CONFIRMED', 'PAYMENT_PENDING', 'IN_USE') " +
           "AND b.endDate > :now")
    boolean hasActiveBooking(@Param("carId") Integer carId, @Param("now") LocalDateTime now);

    /**
     * Find available cars for rental search with filtering conditions:
     * 1. Car location contains the search location (case-insensitive)
     * 2. Car status is AVAILABLE (not RESERVED, not BOOKED, not UNAVAILABLE)
     * 3. Car does NOT have any overlapping bookings with the requested period
     *
     * Overlap rule: requestedStart < existingEnd AND requestedEnd > existingStart
     */
    @Query("SELECT DISTINCT c FROM Car c " +
           "WHERE LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')) " +
           "AND c.status = 'AVAILABLE' " +
           "AND c.carId NOT IN (" +
           "    SELECT b.car.carId FROM Booking b " +
           "    WHERE b.status IN ('CONFIRMED', 'PAYMENT_PENDING') " +
           "    AND b.startDate < :endDate " +
           "    AND b.endDate > :startDate" +
           ")")
    List<Car> findAvailableCarsForRental(
            @Param("location") String location,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get all distinct locations (provinces/cities) from cars
     */
    @Query("SELECT DISTINCT c.location FROM Car c WHERE c.location IS NOT NULL AND c.status = 'AVAILABLE' ORDER BY c.location")
    List<String> findAllDistinctLocations();

    // ===== Admin Dashboard queries =====

    /**
     * Count cars by status
     */
    @Query("SELECT COUNT(c) FROM Car c WHERE c.status = :status")
    Long countByStatus(@Param("status") CarStatus status);

    /**
     * Count cars created after a specific date (for "new this month" stats)
     */
    @Query("SELECT COUNT(c) FROM Car c WHERE c.createdAt >= :startDate")
    Long countCarsCreatedAfter(@Param("startDate") LocalDateTime startDate);

    /**
     * Count pending car approvals
     */
    @Query("SELECT COUNT(c) FROM Car c WHERE c.status = 'PENDING'")
    Long countPendingApprovals();
}
