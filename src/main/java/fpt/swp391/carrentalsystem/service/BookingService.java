package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.CreateBookingRequest;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentInfoDto;
import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;

import java.util.List;

public interface BookingService {

    BookingConfirmationDto createBooking(CreateBookingRequest request, Long userId);

    PaymentInfoDto getPaymentInfo(Integer bookingId);

    void confirmPayment(Integer bookingId);

    void releaseExpiredBooking(Integer bookingId);

    List<CarResponseDto> getAvailableCars();

    /**
     * Get rental history for customer
     */
    List<RentalHistoryDto> getCustomerRentalHistory(Long customerId);

    /**
     * Cancel booking before payment
     */
    void cancelBookingBeforePayment(Integer bookingId, Long userId);

    /**
     * Cancel booking after payment (no refund for deposit)
     */
    void cancelBookingAfterPayment(Integer bookingId, Long userId);

    /**
     * Get booking details
     */
    RentalHistoryDto getBookingDetails(Integer bookingId);
}
