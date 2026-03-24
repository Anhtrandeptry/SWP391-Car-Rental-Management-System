package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepositoryByThanhQC extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE c.status = fpt.swp391.carrentalsystem.enums.CarStatus.AVAILABLE " +
            "AND (:location IS NULL OR :location = '' OR c.location = :location) " +
            "AND (:brand IS NULL OR :brand = '' OR c.brand = :brand) " +
            "AND (:seats IS NULL OR c.seats = :seats) " +
            "AND (:carType IS NULL OR :carType = '' OR c.carType = :carType) " +
            "AND (:fuelType IS NULL OR :fuelType = '' OR CAST(c.fuelType AS string) = :fuelType) " +
            "AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (cast(:startDate as timestamp) IS NULL OR c.id NOT IN (" +
            "   SELECT b.car.id FROM Booking b " +
            "   WHERE b.status IN (fpt.swp391.carrentalsystem.enums.BookingStatus.CONFIRMED, " +
            "                      fpt.swp391.carrentalsystem.enums.BookingStatus.PENDING) " +
            "   AND b.startDate < :endDate " +
            "   AND b.endDate > :startDate" +
            "))")
    List<Car> searchCarsCombined(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("location") String location,
            @Param("name") String name,
            @Param("brand") String brand,
            @Param("seats") Integer seats,
            @Param("carType") String carType,
            @Param("fuelType") String fuelType
    );

    @Query("SELECT DISTINCT c.location FROM Car c WHERE c.location IS NOT NULL AND c.location <> ''")
    List<String> findDistinctLocations();

    @Query("SELECT DISTINCT c.brand FROM Car c WHERE c.brand IS NOT NULL")
    List<String> findDistinctBrands();

    @Query("SELECT DISTINCT c.carType FROM Car c WHERE c.carType IS NOT NULL")
    List<String> findDistinctCarTypes();

    @Query("SELECT DISTINCT CAST(c.fuelType AS string) FROM Car c WHERE c.fuelType IS NOT NULL")
    List<String> findDistinctFuelTypes();

    @Query("SELECT DISTINCT c.seats FROM Car c WHERE c.seats IS NOT NULL")
    List<Integer> findDistinctSeats();

    List<Car> findByOwner_Id(Long ownerId);

    List<Car> findByStatus(CarStatus status);

    @Query("SELECT c FROM Car c LEFT JOIN FETCH c.images WHERE c.carId = :id")
    Optional<Car> findByIdWithImages(@Param("id") Long id);
}