package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.mapper.CarMapper;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;

    private static final int RESERVATION_TIMEOUT_MINUTES = 5;

    @Override
    @Transactional(readOnly = true)
    public List<CarResponseDto> getAvailableCars() {
        List<Car> availableCars = carRepository.findByStatus(CarStatus.AVAILABLE);
        log.info("Found {} available cars", availableCars.size());
        return carMapper.toDtoList(availableCars);
    }

    @Override
    public boolean reserveCar(Integer carId) {
        try {
            // Use pessimistic lock to prevent race condition
            Car car = carRepository.findByIdWithLock(carId)
                    .orElseThrow(() -> new RuntimeException("Car not found: " + carId));

            // Check if car is available
            if (car.getStatus() != CarStatus.AVAILABLE) {
                log.warn("Car {} is not available. Current status: {}", carId, car.getStatus());
                return false;
            }

            // Reserve the car
            car.setStatus(CarStatus.RESERVED);
            car.setReservationExpireTime(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES));
            carRepository.save(car);

            log.info("Car {} reserved until {}", carId, car.getReservationExpireTime());
            return true;

        } catch (Exception e) {
            log.error("Error reserving car {}: {}", carId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void confirmCarBooking(Integer carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));

        car.setStatus(CarStatus.BOOKED);
        car.setReservationExpireTime(null);
        carRepository.save(car);

        log.info("Car {} booking confirmed, status set to BOOKED", carId);
    }

    @Override
    public void releaseCarReservation(Integer carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));

        // Only release if car is currently reserved
        if (car.getStatus() == CarStatus.RESERVED || car.getStatus() == CarStatus.BOOKED) {
            car.setStatus(CarStatus.AVAILABLE);
            car.setReservationExpireTime(null);
            carRepository.save(car);

            log.info("Car {} reservation released, status set to AVAILABLE", carId);
        }
    }

    @Override
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Car> expiredReservations = carRepository.findExpiredReservations(CarStatus.RESERVED, now);

        for (Car car : expiredReservations) {
            car.setStatus(CarStatus.AVAILABLE);
            car.setReservationExpireTime(null);
            carRepository.save(car);
            log.info("Released expired reservation for car {}", car.getCarId());
        }

        if (!expiredReservations.isEmpty()) {
            log.info("Released {} expired car reservations", expiredReservations.size());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponseDto> getCarsByOwner(Long ownerId) {
        List<Car> ownerCars = carRepository.findByOwnerId(ownerId);
        return carMapper.toDtoList(ownerCars);
    }

    @Override
    public boolean setCarAvailability(Integer carId, Long ownerId, CarStatus status) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));

        // Verify ownership
        if (!car.getOwner().getId().equals(ownerId)) {
            log.warn("User {} attempted to modify car {} owned by {}", ownerId, carId, car.getOwner().getId());
            throw new RuntimeException("You don't have permission to modify this car");
        }

        // Check if car has active booking
        if (hasActiveBooking(carId)) {
            log.warn("Cannot change status of car {} - has active booking", carId);
            throw new RuntimeException("Car has active booking. Cannot change status until booking ends.");
        }

        // Only allow setting to AVAILABLE or UNAVAILABLE
        if (status != CarStatus.AVAILABLE && status != CarStatus.UNAVAILABLE) {
            throw new RuntimeException("Invalid status. Only AVAILABLE or UNAVAILABLE allowed.");
        }

        car.setStatus(status);
        carRepository.save(car);

        log.info("Car {} status changed to {} by owner {}", carId, status, ownerId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveBooking(Integer carId) {
        return carRepository.hasActiveBooking(carId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Car getCarById(Integer carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carId));
    }
}

