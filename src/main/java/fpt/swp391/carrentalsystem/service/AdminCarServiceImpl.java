package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of AdminCarService
 * Handles admin operations for car approval/rejection
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCarServiceImpl implements AdminCarService {

    private final CarRepository carRepository;

    @Override
    public List<Car> getPendingCars() {
        log.info("Fetching all pending cars for admin review");
        List<Car> cars = carRepository.findByStatus(CarStatus.PENDING);
        log.info("DEBUG - Repository returned {} PENDING cars", cars.size());
        return cars;
    }

    @Override
    public List<Car> getAllCars() {
        log.info("Fetching all cars for admin overview");
        List<Car> allCars = carRepository.findAll();
        log.info("DEBUG - Total cars in database: {}", allCars.size());
        // Log status breakdown
        for (Car car : allCars) {
            log.info("DEBUG - Car ID={}, Name={}, Status={}", car.getId(), car.getName(), car.getStatus());
        }
        return allCars;
    }

    @Override
    @Transactional
    public void approveCar(Long carId) {
        log.info("Approving car with ID: {}", carId);

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + carId));

        if (car.getStatus() != CarStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể duyệt xe đang ở trạng thái CHỜ DUYỆT. Trạng thái hiện tại: " + car.getStatus().getDisplayName());
        }

        // Set status to AVAILABLE (ready for rent) instead of APPROVED
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);

        log.info("Car {} approved and set to AVAILABLE successfully", carId);
    }

    @Override
    @Transactional
    public void rejectCar(Long carId) {
        log.info("Rejecting car with ID: {}", carId);

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe với ID: " + carId));

        if (car.getStatus() != CarStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể từ chối xe đang ở trạng thái CHỜ DUYỆT. Trạng thái hiện tại: " + car.getStatus().getDisplayName());
        }

        car.setStatus(CarStatus.REJECTED);
        carRepository.save(car);

        log.info("Car {} rejected successfully", carId);
    }

    @Override
    public Car getCarById(Long carId) {
        return carRepository.findById(carId).orElse(null);
    }

    @Override
    public long countPendingCars() {
        return carRepository.countByStatus(CarStatus.PENDING);
    }

    @Override
    public List<Car> getAvailableCars() {
        log.info("Fetching all available cars");
        return carRepository.findByStatus(CarStatus.AVAILABLE);
    }

    @Override
    public List<Car> getRejectedCars() {
        log.info("Fetching all rejected cars");
        return carRepository.findByStatus(CarStatus.REJECTED);
    }

    @Override
    public long countAvailableCars() {
        return carRepository.countByStatus(CarStatus.AVAILABLE);
    }

    @Override
    public long countRejectedCars() {
        return carRepository.countByStatus(CarStatus.REJECTED);
    }
}
