package fpt.swp391.carrentalsystem.service.impl;

import fpt.swp391.carrentalsystem.dto.CarListItemDto;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.service.CarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<CarListItemDto> getPendingCars() {
        return carRepository.findByStatus("Pending").stream()
                .map(CarListItemDto::fromEntity)
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
    public List<CarListItemDto> filterCars(String name, Integer seats, String brand, String carType, String fuelType, String location) {
        return carRepository.filterAvailableCars(name, seats, brand, carType, fuelType, location)
                .stream()
                .map(CarListItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarListItemDto> listAll() {
        return carRepository.findAllAvailable().stream()
                .map(CarListItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarListItemDto> searchByName(String name) {
        return carRepository.findByNameContainingIgnoreCaseAndStatus(name, "Available")
                .stream()
                .map(CarListItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Car getCarById(Long id) {
        Car car = carRepository.findById(id).orElse(null);
        if (car != null && !"Available".equals(car.getStatus())) {
            return null;
        }
        return car;
    }

    @Override public List<String> getAllBrands() { return carRepository.findDistinctBrands(); }
    @Override public List<String> getAllCarTypes() { return carRepository.findDistinctCarTypes(); }
    @Override public List<String> getAllFuelTypes() { return carRepository.findDistinctFuelTypes(); }
    @Override public List<Integer> getAllSeats() { return carRepository.findDistinctSeats(); }
}