package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import java.math.BigDecimal;

public interface PaymentService {

    /**
     * Generate VietQR payment URL
     */
    String generateVietQRUrl(Integer bookingId, BigDecimal amount, String description);

    /**
     * Generate QR code as base64 image
     */
    String generateQRCode(Integer bookingId, BigDecimal amount) throws Exception;

    /**
     * Get payment info for a booking
     */
    PaymentResponseDto getPaymentInfo(Integer bookingId);
}

