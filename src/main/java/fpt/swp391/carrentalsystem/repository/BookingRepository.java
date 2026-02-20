package fpt.swp391.carrentalsystem.repository;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.car.carId = :carId " +
           "AND b.status = :status " +
           "AND ((b.startDate < :endDate AND b.endDate > :startDate))")
    long countOverlappingBookings(Integer carId, LocalDateTime startDate, 
                                   LocalDateTime endDate, BookingStatus status);
}
