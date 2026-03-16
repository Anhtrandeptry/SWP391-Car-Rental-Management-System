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
    List<Car> findByOwnerIdAndStatus(Long ownerId, CarStatus status);

    List<Car> findByOwnerId(Long ownerId);

    // Find car with pessimistic lock for reservation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Car c WHERE c.carId = :carId")
    Optional<Car> findByIdWithLock(@Param("carId") Integer carId);

    // Find expired reservations
    @Query("SELECT c FROM Car c WHERE c.status = :status AND c.reservationExpireTime < :now")
    List<Car> findExpiredReservations(@Param("status") CarStatus status, @Param("now") LocalDateTime now);

    // Check if car has active booking
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.car.carId = :carId " +
            "AND b.status IN ('CONFIRMED', 'PAYMENT_PENDING') " +
            "AND b.endDate > :now")
    boolean hasActiveBooking(@Param("carId") Integer carId, @Param("now") LocalDateTime now);
}
