package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.BookingHistoryResponse;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public List<BookingHistoryResponse> getCustomerBookingHistory(User customer) {
        List<Booking> bookings = bookingRepository.findByCustomerOrderByCreatedAtDesc(customer);

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingHistoryResponse mapToResponse(Booking booking) {
        long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        if (days <= 0) days = 1;

        return BookingHistoryResponse.builder()
                .bookingId(booking.getBookingId())
                .bookingCode("BK-" + String.format("%04d", booking.getBookingId()))
                .carName(booking.getCar().getBrand() + " " + booking.getCar().getModel())
                .licensePlate(booking.getCar().getLicensePlate())
                .carImage(booking.getCar().getImages().isEmpty() ? null : booking.getCar().getImages().get(0).getImageUrl())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .durationDays(days)
                .pickupLocation(booking.getPickupLocation())
                .ownerName(booking.getCar().getOwner().getFirstName() + " " + booking.getCar().getOwner().getLastName())
                .ownerPhone(booking.getCar().getOwner().getPhoneNumber())
                .totalAmount(booking.getTotalAmount())
                .pricePerDay(booking.getCar().getPricePerDay())
                .status(booking.getStatus().name())
                .build();
    }
}