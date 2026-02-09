package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT c.brand FROM Car c WHERE c.brand IS NOT NULL")
    List<String> findDistinctBrands();

    @Query("SELECT DISTINCT c.carType FROM Car c WHERE c.carType IS NOT NULL")
    List<String> findDistinctCarTypes();

    @Query("SELECT DISTINCT c.fuelType FROM Car c WHERE c.fuelType IS NOT NULL")
    List<String> findDistinctFuelTypes();

    @Query("SELECT DISTINCT c.seats FROM Car c WHERE c.seats IS NOT NULL")
    List<Integer> findDistinctSeats();
}
