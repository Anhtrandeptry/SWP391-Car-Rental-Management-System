package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.CarListItemDto;
import fpt.swp391.carrentalsystem.entity.Car;
import java.util.List;

public interface CarService {
    List<CarListItemDto> filterCars(String name, Integer seats, String brand, String carType, String fuelType, String location);
    List<String> getAllBrands();
    List<String> getAllCarTypes();
    List<String> getAllFuelTypes();
    List<Integer> getAllSeats();
    Car getCarById(Long id);
    List<CarListItemDto> listAll();
    List<CarListItemDto> searchByName(String name);


    List<CarListItemDto> getPendingCars();
    void approveCar(Long id);
    void rejectCar(Long id);
    List<CarListItemDto> getCarsByOwner(Long ownerId);
}