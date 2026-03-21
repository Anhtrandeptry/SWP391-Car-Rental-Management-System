package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.CarListItemResponse;
import fpt.swp391.carrentalsystem.entity.Car;
import java.util.List;

public interface CarServiceByThanhQC {
    List<CarListItemResponse> searchCars(String location, String startDate, String startTime,
                                         String endDate, String endTime, String name,
                                         Integer seats, String brand, String carType, String fuelType);

    List<String> getAllLocations();
    List<String> getAllBrands();
    List<String> getAllCarTypes();
    List<String> getAllFuelTypes();
    List<Integer> getAllSeats();
    Car getCarById(Long id);

    List<CarListItemResponse> getCarsByOwner(Long ownerId);
    List<CarListItemResponse> getPendingCars();
    void approveCar(Long id);
    void rejectCar(Long id);
}