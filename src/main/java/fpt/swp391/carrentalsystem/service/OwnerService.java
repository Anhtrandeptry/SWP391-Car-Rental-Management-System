package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.OwnerIncomeDto;
import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.enums.CarStatus;

import java.util.List;

public interface OwnerService {

    /**
     * Get rental history for owner (all bookings for their cars)
     */
    List<RentalHistoryDto> getRentalHistory(Long ownerId);

    /**
     * Get income details for owner
     */
    OwnerIncomeDto getIncomeDetails(Long ownerId);

    /**
     * Get all cars owned by the owner
     */
    List<CarResponseDto> getOwnerCars(Long ownerId);

    /**
     * Set car availability (AVAILABLE or UNAVAILABLE)
     */
    boolean setCarAvailability(Integer carId, Long ownerId, CarStatus status);
}

