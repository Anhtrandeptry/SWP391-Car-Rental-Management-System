package fpt.swp391.carrentalsystem.repository;
import fpt.swp391.carrentalsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CarRepository extends JpaRepository<Car, Integer> {
}
