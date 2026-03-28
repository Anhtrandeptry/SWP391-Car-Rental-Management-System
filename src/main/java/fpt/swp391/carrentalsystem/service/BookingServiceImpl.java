package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.CreateBookingRequest;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentInfoDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.PaymentStatus;
import fpt.swp391.carrentalsystem.mapper.BookingMapper;
import fpt.swp391.carrentalsystem.mapper.CarMapper;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;
    private final CarMapper carMapper;
    private final PaymentService paymentService;

    private static final BigDecimal HOLDING_FEE = BigDecimal.valueOf(500000);
    private static final BigDecimal DEPOSIT_AMOUNT = BigDecimal.valueOf(5000000);
    private static final int PAYMENT_TIMEOUT_MINUTES = 5;

    @Override
    public BookingConfirmationDto createBooking(CreateBookingRequest request, Long userId) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Use pessimistic lock to prevent race condition
        Car car = carRepository.findByIdWithLock(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        // Validate dates
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        // Check if car is available (status check)
        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new RuntimeException("Car is not available for booking. Current status: " + car.getStatus());
        }

        // Check for overlapping bookings
        boolean isAvailable = isCarAvailable(car.getCarId(), request.getStartDate(), request.getEndDate());
        if (!isAvailable) {
            throw new RuntimeException("Car is not available for selected dates");
        }

        // Reserve the car
        car.setStatus(CarStatus.RESERVED);
        car.setReservationExpireTime(LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES));
        carRepository.save(car);

        // Calculate fees
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days == 0) days = 1;

        BigDecimal rentalFee = car.getPricePerDay().multiply(BigDecimal.valueOf(days));
        BigDecimal totalAmount = rentalFee.add(HOLDING_FEE).add(DEPOSIT_AMOUNT);

        // Determine pickup location - use car's location if not provided or empty
        String pickupLocation = (request.getPickupLocation() != null && !request.getPickupLocation().trim().isEmpty())
                ? request.getPickupLocation().trim()
                : car.getLocation();

        // Create booking
        Booking booking = Booking.builder()
                .customer(customer)
                .car(car)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .pickupLocation(pickupLocation)
                .rentalFee(rentalFee)
                .holdingFee(HOLDING_FEE)
                .depositAmount(DEPOSIT_AMOUNT)
                .totalAmount(totalAmount)
                .status(BookingStatus.PAYMENT_PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentDeadline(LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES))
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created: {} for car: {} (reserved until {})",
                savedBooking.getBookingId(), car.getCarId(), car.getReservationExpireTime());

        return bookingMapper.toConfirmationDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentInfoDto getPaymentInfo(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return PaymentInfoDto.builder()
                .bookingId(bookingId)
                .holdingFee(booking.getHoldingFee())
                .depositAmount(booking.getDepositAmount())
                .rentalFee(booking.getRentalFee())
                .totalAmount(booking.getTotalAmount())
                .build();
    }

    @Override
    public PaymentResponseDto initiatePayOSPayment(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if booking is still pending payment
        if (booking.getStatus() != BookingStatus.PAYMENT_PENDING) {
            throw new RuntimeException("Booking is not in pending payment status");
        }

        // Check if payment deadline has passed
        if (booking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Payment deadline has passed. Please create a new booking.");
        }

        String description = "Phi giu cho Booking " + bookingId;
        return paymentService.createPayOSPayment(bookingId, booking.getHoldingFee(), description);
    }

    @Override
    public void confirmPayment(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if payment deadline has passed
        if (booking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Payment deadline has passed. Please create a new booking.");
        }

        // Update booking status
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Update car status to BOOKED and clear reservation time
        Car car = booking.getCar();
        car.setStatus(CarStatus.BOOKED);
        car.setReservationExpireTime(null);
        carRepository.save(car);

        // Send notifications
        notificationService.sendPaymentSuccessEmail(booking);
        notificationService.sendOwnerNotification(booking);

        log.info("Payment confirmed for booking: {}, car status set to BOOKED", bookingId);
    }

    @Override
    public void releaseExpiredBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getPaymentDeadline().isBefore(LocalDateTime.now())
                && booking.getPaymentStatus() == PaymentStatus.UNPAID) {

            // Cancel the booking
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            // Release the car reservation
            Car car = booking.getCar();
            if (car.getStatus() == CarStatus.RESERVED) {
                car.setStatus(CarStatus.AVAILABLE);
                car.setReservationExpireTime(null);
                carRepository.save(car);
                log.info("Car {} released back to AVAILABLE due to expired booking", car.getCarId());
            }

            // Send cancellation notification
            notificationService.sendBookingCancelledEmail(booking);

            log.info("Expired booking cancelled: {}", bookingId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponseDto> getAvailableCars() {
        List<Car> cars = carRepository.findByStatus(CarStatus.AVAILABLE);
        log.info("Fetched {} available cars for booking page", cars.size());
        return carMapper.toDtoList(cars);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalHistoryDto> getCustomerRentalHistory(Long customerId) {
        // Use optimized query with JOIN FETCH
        List<Booking> bookings = bookingRepository.findCustomerBookingsWithDetails(customerId);
        return bookings.stream()
                .map(this::toRentalHistoryDtoForCustomer)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalHistoryDto> getOwnerRentalHistory(Long ownerId) {
        // Use optimized query with JOIN FETCH
        List<Booking> bookings = bookingRepository.findOwnerBookingsWithDetails(ownerId);
        return bookings.stream()
                .map(this::toRentalHistoryDtoForOwner)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBookingBeforePayment(Integer bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify ownership
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to cancel this booking");
        }

        // Can only cancel if payment is pending
        if (booking.getStatus() != BookingStatus.PAYMENT_PENDING) {
            throw new RuntimeException("Cannot cancel. Booking status is: " + booking.getStatus());
        }

        // Cancel booking
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Release car reservation
        Car car = booking.getCar();
        if (car.getStatus() == CarStatus.RESERVED) {
            car.setStatus(CarStatus.AVAILABLE);
            car.setReservationExpireTime(null);
            carRepository.save(car);
        }

        log.info("Booking {} cancelled before payment by user {}", bookingId, userId);
    }

    @Override
    public void cancelBookingAfterPayment(Integer bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify ownership
        if (!booking.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to cancel this booking");
        }

        // Can only cancel if confirmed and paid
        if (booking.getStatus() != BookingStatus.CONFIRMED || booking.getPaymentStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Cannot cancel. Booking is not in CONFIRMED status or not PAID");
        }

        // Cancel booking - deposit is NOT refunded
        booking.setStatus(BookingStatus.CANCELLED);
        // Note: paymentStatus remains PAID but booking is CANCELLED
        // This booking will NOT be counted in revenue calculation
        bookingRepository.save(booking);

        // Release car back to available
        Car car = booking.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        car.setReservationExpireTime(null);
        carRepository.save(car);

        // Send cancellation notification
        notificationService.sendBookingCancelledEmail(booking);

        log.info("Booking {} cancelled after payment by user {} (deposit not refunded)", bookingId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalHistoryDto getBookingDetails(Integer bookingId) {
        Booking booking = bookingRepository.findBookingWithAllDetails(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return toRentalHistoryDtoForCustomer(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalHistoryDto getBookingDetailsForCustomer(Integer bookingId, Long customerId) {
        Booking booking = bookingRepository.findBookingWithAllDetails(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the customer owns this booking
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("You don't have permission to view this booking");
        }

        return toRentalHistoryDtoForCustomer(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public RentalHistoryDto getBookingDetailsForOwner(Integer bookingId, Long ownerId) {
        Booking booking = bookingRepository.findBookingWithAllDetails(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify the owner owns the car
        if (!booking.getCar().getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You don't have permission to view this booking");
        }

        return toRentalHistoryDtoForOwner(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canCancelBooking(Integer bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check ownership
        if (!booking.getCustomer().getId().equals(userId)) {
            return false;
        }

        // Can cancel if: status is PENDING or PAYMENT_PENDING AND current time < startDate
        boolean validStatus = booking.getStatus() == BookingStatus.PENDING
                || booking.getStatus() == BookingStatus.PAYMENT_PENDING;
        boolean beforeStartDate = LocalDateTime.now().isBefore(booking.getStartDate());

        return validStatus && beforeStartDate;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canPayBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Can pay if: paymentStatus is UNPAID AND current time < startDate
        boolean unpaid = booking.getPaymentStatus() == PaymentStatus.UNPAID;
        boolean beforeStartDate = LocalDateTime.now().isBefore(booking.getStartDate());
        boolean validStatus = booking.getStatus() == BookingStatus.PAYMENT_PENDING;

        return unpaid && beforeStartDate && validStatus;
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

    private boolean isCarAvailable(Integer carId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.countOverlappingBookings(
                carId, startDate, endDate, BookingStatus.CONFIRMED) == 0;
    }

    private RentalHistoryDto toRentalHistoryDtoForCustomer(Booking booking) {
        User owner = booking.getCar().getOwner();
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
                .ownerName(owner.getFirstName() + " " + owner.getLastName())
                .ownerPhone(owner.getPhoneNumber())
                .ownerEmail(owner.getEmail())
                .build();
    }




    /**
     * Search available cars with raw parameters
     */
    @Override
    @Transactional(readOnly = true)
    public List<CarResponseDto> searchAvailableCars(String location, LocalDateTime startDate, LocalDateTime endDate) {
        // Validate dates
        if (!endDate.isAfter(startDate)) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        if (startDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Ngày bắt đầu không thể trong quá khứ");
        }

        // Find cars that match all criteria using the repository query
        List<Car> availableCars = carRepository.findAvailableCarsForRental(location, startDate, endDate);

        log.info("Found {} available cars for location: {}, period: {} to {}",
                availableCars.size(), location, startDate, endDate);

        return carMapper.toDtoList(availableCars);
    }

    /**
     * Get all distinct locations (provinces/cities) for dropdown
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllLocations() {
        List<String> locations = carRepository.findAllDistinctLocations();
        log.info("Found {} distinct locations", locations.size());
        return locations;
    }
}
