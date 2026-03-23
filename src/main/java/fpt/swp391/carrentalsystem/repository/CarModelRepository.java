package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long> {

    List<CarModel> findByBrandId(Long brandId);

    List<CarModel> findByBrandName(String brandName);
}


