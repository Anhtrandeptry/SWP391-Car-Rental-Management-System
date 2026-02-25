package fpt.swp391.carrentalsystem.service;
import fpt.swp391.carrentalsystem.dto.request.CreateBookingRequest;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentInfoDto;

import java.util.List;

public interface BookingService {
    BookingConfirmationDto createBooking(CreateBookingRequest request, Long userId);
    PaymentInfoDto getPaymentInfo(Integer bookingId);
    void confirmPayment(Integer bookingId);
    void releaseExpiredBooking(Integer bookingId);
    List<CarResponseDto> getAvailableCars();
}
