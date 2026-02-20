package fpt.swp391.carrentalsystem.service;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
@Component
@RequiredArgsConstructor
public class BookingScheduler {
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    @Scheduled(fixedDelay = 60000)
    public void releaseExpiredBookings() {
        bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.PAYMENT_PENDING 
                        && b.getPaymentStatus() == PaymentStatus.UNPAID
                        && b.getPaymentDeadline().isBefore(LocalDateTime.now()))
                .forEach(b -> bookingService.releaseExpiredBooking(b.getBookingId()));
    }
}
