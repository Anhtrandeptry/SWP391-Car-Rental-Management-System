package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.*;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.PaymentStatus;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OwnerServiceImpl implements OwnerService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarService carService;

    @Override
    @Transactional(readOnly = true)
    public List<RentalHistoryDto> getRentalHistory(Long ownerId) {
        // Use optimized query with JOIN FETCH to avoid N+1 problem
        List<Booking> bookings = bookingRepository.findOwnerBookingsWithDetails(ownerId);

        return bookings.stream()
                .map(this::toRentalHistoryDtoForOwner)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerIncomeDto getIncomeDetails(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));

        List<Car> ownerCars = carRepository.findByOwner_Id(ownerId);
        BigDecimal totalIncome = bookingRepository.calculateTotalRevenueByOwnerId(ownerId);

        List<CarIncomeDto> carIncomes = new ArrayList<>();
        int totalCompletedBookings = 0;

        for (Car car : ownerCars) {
            List<Booking> carBookings = bookingRepository.findByCarCarIdOrderByCreatedAtDesc(car.getCarId());
            BigDecimal carIncome = bookingRepository.calculateRevenueByCarId(car.getCarId());

            List<BookingIncomeDto> bookingIncomeDtos = carBookings.stream()
                    .filter(b -> b.getPaymentStatus() == PaymentStatus.PAID &&
                               (b.getStatus() == BookingStatus.CONFIRMED ||
                                b.getStatus() == BookingStatus.COMPLETED ||
                                b.getStatus() == BookingStatus.IN_USE))
                    .map(this::toBookingIncomeDto)
                    .collect(Collectors.toList());

            int completedBookingsCount = (int) carBookings.stream()
                    .filter(b -> b.getPaymentStatus() == PaymentStatus.PAID &&
                               (b.getStatus() == BookingStatus.CONFIRMED ||
                                b.getStatus() == BookingStatus.COMPLETED ||
                                b.getStatus() == BookingStatus.IN_USE))
                    .count();

            totalCompletedBookings += completedBookingsCount;

            carIncomes.add(CarIncomeDto.builder()
                    .carId(car.getCarId())
                    .carName(car.getName())
                    .licensePlate(car.getLicensePlate())
                    .status(car.getStatus().name())
                    .totalIncome(carIncome)
                    .totalBookings(carBookings.size())
                    .completedBookings(completedBookingsCount)
                    .bookings(bookingIncomeDtos)
                    .build());
        }

        return OwnerIncomeDto.builder()
                .ownerId(ownerId)
                .ownerName(owner.getFirstName() + " " + owner.getLastName())
                .totalIncome(totalIncome)
                .totalCars(ownerCars.size())
                .totalCompletedBookings(totalCompletedBookings)
                .carIncomes(carIncomes)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponseDto> getOwnerCars(Long ownerId) {
        return carService.getCarsByOwner(ownerId);
    }

    @Override
    public boolean setCarAvailability(Integer carId, Long ownerId, CarStatus status) {
        return carService.setCarAvailability(carId, ownerId, status);
    }

    private RentalHistoryDto toRentalHistoryDtoForOwner(Booking booking) {
        User customer = booking.getCustomer();
        Car car = booking.getCar();

        return RentalHistoryDto.builder()
                .bookingId(booking.getBookingId())
                .carId(car.getCarId())
                .carName(car.getName())
                .carBrand(car.getBrand())
                .carModel(car.getModel())
                .licensePlate(car.getLicensePlate())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .pickupLocation(booking.getPickupLocation())
                .rentalFee(booking.getRentalFee())
                .depositAmount(booking.getDepositAmount())
                .holdingFee(booking.getHoldingFee())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .paymentStatus(booking.getPaymentStatus().name())
                .createdAt(booking.getCreatedAt())
                .customerName(customer.getFirstName() + " " + customer.getLastName())
                .customerPhone(customer.getPhoneNumber())
                .customerEmail(customer.getEmail())
                .build();
    }

    private BookingIncomeDto toBookingIncomeDto(Booking booking) {
        return BookingIncomeDto.builder()
                .bookingId(booking.getBookingId())
                .customerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .rentalFee(booking.getRentalFee())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}



