package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse;
import fpt.swp391.carrentalsystem.entity.Car;
import java.util.List;

public interface CarService {
    List<CarListItemResponse> filterCars(String name, Integer seats, String brand, String carType, String fuelType, String location);

    List<String> getAllBrands();
    List<String> getAllCarTypes();
    List<String> getAllFuelTypes();
    List<Integer> getAllSeats();

    Car getCarById(Long id);

    List<CarListItemResponse> listAll();
    List<CarListItemResponse> searchByName(String name);

    List<CarListItemResponse> getPendingCars();
    void approveCar(Long id);
    void rejectCar(Long id);

    List<CarListItemResponse> getCarsByOwner(Long ownerId);
}