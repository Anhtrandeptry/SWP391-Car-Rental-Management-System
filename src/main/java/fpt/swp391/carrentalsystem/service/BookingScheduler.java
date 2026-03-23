package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final CarRepository carRepository;

    /**
     * Run every minute to check for expired payment pending bookings
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void releaseExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        // Find and release expired payment pending bookings
        List<Booking> expiredBookings = bookingRepository.findExpiredPaymentPendingBookings(
                BookingStatus.PAYMENT_PENDING, PaymentStatus.UNPAID, now);

        for (Booking booking : expiredBookings) {
            try {
                bookingService.releaseExpiredBooking(booking.getBookingId());
            } catch (Exception e) {
                log.error("Error releasing expired booking {}: {}", booking.getBookingId(), e.getMessage());
            }
        }

        if (!expiredBookings.isEmpty()) {
            log.info("Released {} expired bookings", expiredBookings.size());
        }
    }

    /**
     * Run every minute to release expired car reservations
     * This is a backup mechanism in case the booking scheduler missed some
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void releaseExpiredCarReservations() {
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
}
