package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {

    /**
     * Create PayOS payment link for a booking
     * @param bookingId The booking ID (for reference)
     * @param orderCode Globally unique order code for PayOS
     * @param amount The payment amount
     * @param description Description of the payment
     * @return PaymentResponseDto containing the checkout URL
     */
    PaymentResponseDto createPayOSPayment(Integer bookingId, Long orderCode, BigDecimal amount, String description);

    /**
     * Process PayOS webhook callback
     * @param webhookData Webhook data from PayOS
     * @return true if processed successfully
     */
    boolean processPayOSWebhook(Map<String, Object> webhookData);

    /**
     * Verify PayOS webhook signature
     * @param webhookData Webhook data from PayOS
     * @return true if signature is valid
     */
    boolean verifyPayOSWebhook(Map<String, Object> webhookData);

    /**
     * Get payment info for a booking
     * @param bookingId The booking ID
     * @return PaymentResponseDto
     */
    PaymentResponseDto getPaymentInfo(Integer bookingId);

    /**
     * Cancel a PayOS payment
     * @param orderCode The order code (bookingId)
     * @return true if cancelled successfully
     */
    boolean cancelPayOSPayment(Long orderCode);
}


