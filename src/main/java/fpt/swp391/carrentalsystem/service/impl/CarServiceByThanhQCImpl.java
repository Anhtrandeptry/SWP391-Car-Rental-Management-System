package fpt.swp391.carrentalsystem.service.impl;

import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.mapper.CarMapper;
import fpt.swp391.carrentalsystem.repository.CarRepositoryByThanhQC;
import fpt.swp391.carrentalsystem.service.CarServiceByThanhQC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceByThanhQCImpl implements CarServiceByThanhQC {

    private final CarRepositoryByThanhQC carRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarListItemResponse> searchCars(String location, String startDate, String startTime, String endDate, String endTime, String name, Integer seats, String brand, String carType, String fuelType) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            start = LocalDateTime.parse(startDate + " " + (startTime != null ? startTime : "09:00"), formatter);
            end = LocalDateTime.parse(endDate + " " + (endTime != null ? endTime : "09:00"), formatter);
        }

        return carRepository.searchCarsCombined(start, end, location, name, brand, seats, carType, fuelType)
                .stream().map(carMapper::toListItemResponse).collect(Collectors.toList());
    }

    @Override
    public List<CarListItemResponse> getCarsByOwnerAndStatus(Long ownerId, String filter) {
        // Lấy tất cả xe của chủ xe này
        List<Car> allCars = carRepository.findByOwner_Id(ownerId);

        return allCars.stream()
                .filter(car -> {
                    if (filter == null || "all".equalsIgnoreCase(filter)) return true;

                    // Logic lọc dựa trên Enum CarStatus
                    switch (filter.toLowerCase()) {
                        case "pending":
                            return car.getStatus() == CarStatus.PENDING;
                        case "available":
                            return car.getStatus() == CarStatus.AVAILABLE;
                        case "rented":
                            return car.getStatus() == CarStatus.RENTED; // Đảm bảo Enum có RENTED
                        case "inactive":
                            return car.getStatus() == CarStatus.INACTIVE; // Đảm bảo Enum có DELETED
                        default:
                            return true;
                    }
                })
                .map(carMapper::toListItemResponse)
                .collect(Collectors.toList());
    }

    @Override public List<String> getAllLocations() { return carRepository.findDistinctLocations(); }
    @Override public List<String> getAllBrands() { return carRepository.findDistinctBrands(); }
    @Override public List<String> getAllCarTypes() { return carRepository.findDistinctCarTypes(); }
    @Override public List<String> getAllFuelTypes() { return carRepository.findDistinctFuelTypes(); }
    @Override public List<Integer> getAllSeats() { return carRepository.findDistinctSeats(); }
    @Override public Car getCarById(Integer id) { return carRepository.findByIdWithImages(id).orElse(null); }

    @Override public List<CarListItemResponse> getCarsByOwner(Long ownerId) {
        return carRepository.findByOwner_Id(ownerId).stream().map(carMapper::toListItemResponse).collect(Collectors.toList());
    }

    @Override
    public List<CarListItemResponse> getPendingCars() {
        // Thay "Pending" bằng CarStatus.PENDING (hoặc giá trị tương ứng trong Enum của bạn)
        return carRepository.findByStatus(CarStatus.PENDING)
                .stream()
                .map(carMapper::toListItemResponse)
                .collect(Collectors.toList());
    }

    @Override @Transactional
    public void approveCar(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);
    }

    @Override @Transactional
    public void rejectCar(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        car.setStatus(CarStatus.REJECTED);
        carRepository.save(car);
    }

    @Override
    @Transactional
    public void updateCarStatus(Integer id, CarStatus status) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));
        car.setStatus(status);
        carRepository.save(car);
    }
}