package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.entity.Car;

import java.util.List;

/**
 * Service interface for Admin car management operations
 * Handles car approval/rejection workflow
 */
public interface AdminCarService {

    /**
     * Get all cars with PENDING status waiting for admin review
     * @return List of pending cars
     */
    List<Car> getPendingCars();

    /**
     * Get all cars in the system (for admin overview)
     * @return List of all cars
     */
    List<Car> getAllCars();

    /**
     * Approve a car - changes status from PENDING to APPROVED
     * @param carId The ID of the car to approve
     * @throws IllegalArgumentException if car not found
     * @throws IllegalStateException if car is not in PENDING status
     */
    void approveCar(Long carId);

    /**
     * Reject a car - changes status from PENDING to REJECTED
     * @param carId The ID of the car to reject
     * @throws IllegalArgumentException if car not found
     * @throws IllegalStateException if car is not in PENDING status
     */
    void rejectCar(Long carId);

    /**
     * Get a car by ID
     * @param carId The car ID
     * @return The car entity or null if not found
     */
    Car getCarById(Long carId);

    /**
     * Count cars by status for dashboard statistics
     * @return Count of pending cars
     */
    long countPendingCars();

    /**
     * Get all cars with AVAILABLE status (approved and ready for rent)
     * @return List of available cars
     */
    List<Car> getAvailableCars();

    /**
     * Get all cars with REJECTED status
     * @return List of rejected cars
     */
    List<Car> getRejectedCars();

    /**
     * Count available cars
     * @return Count of available cars
     */
    long countAvailableCars();

    /**
     * Count rejected cars
     * @return Count of rejected cars
     */
    long countRejectedCars();
}
