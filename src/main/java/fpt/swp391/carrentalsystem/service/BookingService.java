package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.request.CreateBookingRequest;
import fpt.swp391.carrentalsystem.dto.response.BookingConfirmationDto;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentInfoDto;
import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import fpt.swp391.carrentalsystem.dto.response.RentalHistoryDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingConfirmationDto createBooking(CreateBookingRequest request, Long userId);

    PaymentInfoDto getPaymentInfo(Integer bookingId);

    /**
     * Initiate PayOS payment for a booking
     * @param bookingId The booking ID
     * @return PaymentResponseDto containing the PayOS checkout URL
     */
    PaymentResponseDto initiatePayOSPayment(Integer bookingId);

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


    /**
     * Search available cars with raw parameters
     */
    List<CarResponseDto> searchAvailableCars(String location, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get all distinct locations (provinces/cities) for dropdown
     */
    List<String> getAllLocations();
}
