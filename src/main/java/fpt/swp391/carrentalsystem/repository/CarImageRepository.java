package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {

    List<CarImage> findByCarOrderByDisplayOrderAsc(Car car);

    List<CarImage> findByCarAndIsMainTrue(Car car);
}
