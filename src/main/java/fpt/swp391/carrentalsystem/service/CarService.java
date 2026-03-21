package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;

import java.util.List;

public interface CarService {

    /**
     * Get all available cars for booking
     */
    List<CarResponseDto> getAvailableCars();

    /**
     * Reserve a car for payment (5 minutes timeout)
     * @return true if reservation successful
     */
    boolean reserveCar(Integer carId);

    /**
     * Confirm car booking after payment success
     */
    void confirmCarBooking(Integer carId);

    /**
     * Release car reservation (timeout or cancelled)
     */
    void releaseCarReservation(Integer carId);

    /**
     * Release all expired reservations
     */
    void releaseExpiredReservations();

    /**
     * Get cars by owner
     */
    List<CarResponseDto> getCarsByOwner(Long ownerId);

    /**
     * Set car availability (for owner)
     * @return true if status change successful
     */
    boolean setCarAvailability(Integer carId, Long ownerId, CarStatus status);

    /**
     * Check if car has active booking
     */
    boolean hasActiveBooking(Integer carId);

    /**
     * Get car by ID
     */
    Car getCarById(Integer carId);
}

