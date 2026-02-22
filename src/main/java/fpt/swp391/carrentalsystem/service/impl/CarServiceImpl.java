package fpt.swp391.carrentalsystem.service.impl;

import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.mapper.CarMapper;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarListItemResponse> getCarsByOwner(Long ownerId) {
        return carRepository.findByOwnerId(ownerId).stream()
                .map(carMapper::toListItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarListItemResponse> getPendingCars() {
        return carRepository.findByStatus("Pending").stream()
                .map(carMapper::toListItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        car.setStatus("Available");
        carRepository.save(car);
    }

    @Override
    @Transactional
    public void rejectCar(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        car.setStatus("Rejected");
        carRepository.save(car);
    }

    @Override
    public List<CarListItemResponse> filterCars(String name, Integer seats, String brand, String carType, String fuelType, String location) {
        return carRepository.filterAvailableCars(name, seats, brand, carType, fuelType, location)
                .stream()
                .map(carMapper::toListItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarListItemResponse> listAll() {
        return carRepository.findAllAvailable().stream()
                .map(carMapper::toListItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarListItemResponse> searchByName(String name) {
        return carRepository.findByNameContainingIgnoreCaseAndStatus(name, "Available")
                .stream()
                .map(carMapper::toListItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    @Override public List<String> getAllBrands() { return carRepository.findDistinctBrands(); }
    @Override public List<String> getAllCarTypes() { return carRepository.findDistinctCarTypes(); }
    @Override public List<String> getAllFuelTypes() { return carRepository.findDistinctFuelTypes(); }
    @Override public List<Integer> getAllSeats() { return carRepository.findDistinctSeats(); }
}