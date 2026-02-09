package fpt.swp391.carrentalsystem.service.impl;

import fpt.swp391.carrentalsystem.dto.CarListItemDto;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.service.CarService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<CarListItemDto> filterCars(
            String name,
            Integer seats,
            String brand,
            String carType,
            String fuelType,
            String location
    ) {
        return carRepository.findAll().stream()
                .filter(c -> name == null || c.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(c -> seats == null || c.getSeats().equals(seats))
                .filter(c -> brand == null || brand.isBlank() || brand.equals(c.getBrand()))
                .filter(c -> carType == null || carType.isBlank() || carType.equals(c.getCarType()))
                .filter(c -> fuelType == null || fuelType.isBlank() || fuelType.equals(c.getFuelType()))
                .filter(c -> location == null || location.isBlank() || c.getLocation().toLowerCase().contains(location.toLowerCase()))
                .map(CarListItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllBrands() {
        return carRepository.findDistinctBrands();
    }

    @Override
    public List<String> getAllCarTypes() {
        return carRepository.findDistinctCarTypes();
    }

    @Override
    public List<String> getAllFuelTypes() {
        return carRepository.findDistinctFuelTypes();
    }

    @Override
    public List<Integer> getAllSeats() {
        return carRepository.findDistinctSeats();
    }

    @Override
    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    @Override
    public List<CarListItemDto> listAll() {
        return carRepository.findAll()
                .stream()
                .map(CarListItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarListItemDto> searchByName(String name) {
        return carRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(CarListItemDto::fromEntity)
                .collect(Collectors.toList());
    }
}
