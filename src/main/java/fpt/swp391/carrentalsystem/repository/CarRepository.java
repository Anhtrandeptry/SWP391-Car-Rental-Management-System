package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(String status);


    List<Car> findByOwnerId(Long ownerId);

    @Query("SELECT c FROM Car c WHERE c.status = 'Available' " +
            "AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:seats IS NULL OR c.seats = :seats) " +
            "AND (:brand IS NULL OR :brand = '' OR c.brand = :brand) " +
            "AND (:carType IS NULL OR :carType = '' OR c.carType = :carType) " +
            "AND (:fuelType IS NULL OR :fuelType = '' OR CAST(c.fuelType AS string) = :fuelType) " +
            "AND (:location IS NULL OR :location = '' OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Car> filterAvailableCars(
            @Param("name") String name,
            @Param("seats") Integer seats,
            @Param("brand") String brand,
            @Param("carType") String carType,
            @Param("fuelType") String fuelType,
            @Param("location") String location
    );

    @Query("SELECT c FROM Car c WHERE c.status = 'Available'")
    List<Car> findAllAvailable();

    @Query("SELECT DISTINCT c.brand FROM Car c WHERE c.brand IS NOT NULL")
    List<String> findDistinctBrands();

    @Query("SELECT DISTINCT c.carType FROM Car c WHERE c.carType IS NOT NULL")
    List<String> findDistinctCarTypes();

    @Query("SELECT DISTINCT c.fuelType FROM Car c WHERE c.fuelType IS NOT NULL")
    List<String> findDistinctFuelTypes();

    @Query("SELECT DISTINCT c.seats FROM Car c WHERE c.seats IS NOT NULL")
    List<Integer> findDistinctSeats();

    List<Car> findByNameContainingIgnoreCaseAndStatus(String name, String status);
}