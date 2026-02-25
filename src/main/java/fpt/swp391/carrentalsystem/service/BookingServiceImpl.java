package fpt.swp391.carrentalsystem.service;
import fpt.swp391.carrentalsystem.dto.request.CreateBookingRequest;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentInfoDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
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
    private static final BigDecimal HOLDING_FEE = BigDecimal.valueOf(500000);
    private static final BigDecimal DEPOSIT_AMOUNT = BigDecimal.valueOf(5000000);
    @Override
    public BookingConfirmationDto createBooking(CreateBookingRequest request, Long userId) {
        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        boolean isAvailable = isCarAvailable(car.getCarId(), request.getStartDate(), request.getEndDate());
        if (!isAvailable) {
            throw new RuntimeException("Car is not available for selected dates");
        }
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days == 0) days = 1;
        BigDecimal rentalFee = car.getPricePerDay().multiply(BigDecimal.valueOf(days));
        BigDecimal totalAmount = rentalFee.add(HOLDING_FEE).add(DEPOSIT_AMOUNT);
        Booking booking = Booking.builder()
                .customer(customer)
                .car(car)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .pickupLocation(request.getPickupLocation() != null ? request.getPickupLocation() : car.getLocation())
                .rentalFee(rentalFee)
                .holdingFee(HOLDING_FEE)
                .depositAmount(DEPOSIT_AMOUNT)
                .totalAmount(totalAmount)
                .status(BookingStatus.PAYMENT_PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentDeadline(LocalDateTime.now().plusMinutes(3))
                .build();
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created: {}", savedBooking.getBookingId());
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
    public void confirmPayment(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Send notifications
        notificationService.sendPaymentSuccessEmail(booking);
        notificationService.sendOwnerNotification(booking);

        log.info("Payment confirmed for booking: {}", bookingId);
    }
    @Override
    public void releaseExpiredBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (booking.getPaymentDeadline().isBefore(LocalDateTime.now())
                && booking.getPaymentStatus() == PaymentStatus.UNPAID) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            // Send cancellation notification
            notificationService.sendBookingCancelledEmail(booking);

            log.info("Expired booking cancelled: {}", bookingId);
        }
    }
    private boolean isCarAvailable(Integer carId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.countOverlappingBookings(
                carId, startDate, endDate, BookingStatus.CONFIRMED) == 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponseDto> getAvailableCars() {

        List<Car> cars = carRepository.findAll();

        log.info("Fetched {} cars for booking page", cars.size());

        return carMapper.toDtoList(cars);
    }


}
