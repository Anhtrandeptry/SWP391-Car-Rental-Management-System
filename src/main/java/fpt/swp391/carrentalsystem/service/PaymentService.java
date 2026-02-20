package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import java.math.BigDecimal;

public interface PaymentService {
    PaymentResponseDto createPaymentRequest(Integer bookingId, BigDecimal amount, String description);
    PaymentResponseDto verifyPayment(String vnpayResponse);
    PaymentResponseDto processPaymentWebhook(String webhookData);
    String generateQRCode(Integer bookingId, BigDecimal amount) throws Exception;
}

