package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.config.PaymentConfig;
import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.Car;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import fpt.swp391.carrentalsystem.enums.CarStatus;
import fpt.swp391.carrentalsystem.enums.PaymentStatus;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final PaymentConfig paymentConfig;
    private final NotificationService notificationService;
    private final WebClient payosWebClient;

    @Override
    public PaymentResponseDto createPayOSPayment(Integer bookingId, BigDecimal amount, String description) {
        try {
            // Use bookingId as orderCode (must be unique and positive)
            long orderCode = bookingId.longValue();
            int amountInt = amount.intValue();

            // Build request body for PayOS API
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("orderCode", orderCode);
            requestBody.put("amount", amountInt);
            requestBody.put("description", description);
            requestBody.put("returnUrl", paymentConfig.getReturnUrl());
            requestBody.put("cancelUrl", paymentConfig.getCancelUrl());

            // Create item list
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", "Phi giu cho - Booking #" + bookingId);
            item.put("quantity", 1);
            item.put("price", amountInt);
            items.add(item);
            requestBody.put("items", items);

            // Generate signature
            String signature = generateSignature(orderCode, amountInt, description,
                    paymentConfig.getCancelUrl(), paymentConfig.getReturnUrl());
            requestBody.put("signature", signature);

            log.info("Creating PayOS payment for booking {}: amount={}", bookingId, amountInt);

            // Call PayOS API
            Map<String, Object> response = payosWebClient.post()
                    .uri("/v2/payment-requests")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Empty response from PayOS");
            }

            String code = String.valueOf(response.get("code"));
            if (!"00".equals(code)) {
                String errorMessage = (String) response.get("desc");
                throw new RuntimeException("PayOS error: " + errorMessage);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            String checkoutUrl = (String) data.get("checkoutUrl");
            String qrCode = (String) data.get("qrCode");

            log.info("Created PayOS payment link for booking {}: checkoutUrl={}", bookingId, checkoutUrl);

            return PaymentResponseDto.builder()
                    .transactionId(String.valueOf(orderCode))
                    .bookingId(bookingId)
                    .amount(amount)
                    .status("PENDING")
                    .paymentUrl(checkoutUrl)
                    .qrCode(qrCode)
                    .message("Redirect to PayOS to complete payment")
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("Error creating PayOS payment for booking {}: {}", bookingId, e.getMessage(), e);
            throw new RuntimeException("Error creating PayOS payment: " + e.getMessage());
        }
    }

    /**
     * Generate PayOS signature using HMAC SHA256
     * Signature format: amount=X&cancelUrl=X&description=X&orderCode=X&returnUrl=X
     */
    private String generateSignature(long orderCode, int amount, String description,
                                     String cancelUrl, String returnUrl) {
        try {
            // Build data string in alphabetical order
            String data = String.format("amount=%d&cancelUrl=%s&description=%s&orderCode=%d&returnUrl=%s",
                    amount, cancelUrl, description, orderCode, returnUrl);

            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    paymentConfig.getChecksumKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error generating PayOS signature: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating signature: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPayOSWebhook(Map<String, Object> webhookData) {
        try {
            if (webhookData == null || webhookData.isEmpty()) {
                log.error("PayOS webhook data is null or empty");
                return false;
            }

            // Check for required fields
            if (!webhookData.containsKey("data") || !webhookData.containsKey("signature")) {
                log.error("PayOS webhook missing required fields");
                return false;
            }

            // Verify signature
            String receivedSignature = (String) webhookData.get("signature");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) webhookData.get("data");

            // Build signature data string from webhook data
            // PayOS webhook signature is computed from the data fields
            String computedSignature = computeWebhookSignature(data);

            if (!receivedSignature.equals(computedSignature)) {
                log.error("PayOS webhook signature mismatch");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Error verifying PayOS webhook: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Compute webhook signature from data
     */
    private String computeWebhookSignature(Map<String, Object> data) {
        try {
            // Sort keys alphabetically and build data string
            TreeMap<String, Object> sortedData = new TreeMap<>(data);
            StringBuilder dataString = new StringBuilder();
            for (Map.Entry<String, Object> entry : sortedData.entrySet()) {
                if (dataString.length() > 0) {
                    dataString.append("&");
                }
                dataString.append(entry.getKey()).append("=").append(entry.getValue());
            }

            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    paymentConfig.getChecksumKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] hashBytes = hmac.doFinal(dataString.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error computing webhook signature: {}", e.getMessage(), e);
            return "";
        }
    }

    @Override
    @Transactional
    public boolean processPayOSWebhook(Map<String, Object> webhookData) {
        try {
            log.info("========================================");
            log.info("=== PROCESSING PAYOS WEBHOOK START ===");
            log.info("========================================");
            log.info("Full webhook data: {}", webhookData);

            // Verify webhook signature first (security)
            if (!verifyPayOSWebhook(webhookData)) {
                log.error("PayOS webhook signature verification failed!");
                return false;
            }
            log.info("Webhook signature verified successfully");

            // Extract data from webhook
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) webhookData.get("data");
            if (data == null) {
                log.error("PayOS webhook missing 'data' field");
                return false;
            }

            log.info("Webhook data content: {}", data);

            // Extract order code (this IS the booking ID directly)
            Object orderCodeObj = data.get("orderCode");
            if (orderCodeObj == null) {
                log.error("PayOS webhook missing orderCode in data");
                return false;
            }

            // orderCode = bookingId (they are the same in createPayOSPayment)
            long orderCode;
            if (orderCodeObj instanceof Number) {
                orderCode = ((Number) orderCodeObj).longValue();
            } else {
                orderCode = Long.parseLong(orderCodeObj.toString());
            }

            // IMPORTANT: orderCode IS the bookingId directly (see createPayOSPayment method)
            Integer bookingId = (int) orderCode;
            log.info(">>> OrderCode: {}, BookingId: {}", orderCode, bookingId);

            // Extract payment status code
            String code = webhookData.get("code") != null ?
                    String.valueOf(webhookData.get("code")) :
                    (data.get("code") != null ? String.valueOf(data.get("code")) : null);

            log.info("PayOS webhook - bookingId: {}, code: {}", bookingId, code);

            // Find booking
            log.info("Looking up booking {} in database...", bookingId);
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                log.error("!!! BOOKING NOT FOUND for ID: {} !!!", bookingId);
                return false;
            }

            Booking booking = bookingOpt.get();
            log.info("Found booking: id={}, status={}, paymentStatus={}",
                    booking.getBookingId(), booking.getStatus(), booking.getPaymentStatus());

            // Check if already processed (idempotency)
            if (booking.getPaymentStatus() == PaymentStatus.PAID) {
                log.info("Booking {} already PAID, skipping duplicate webhook", bookingId);
                return true;
            }

            // Check if payment deadline has passed
            if (booking.getPaymentDeadline() != null &&
                booking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
                log.warn("Payment received AFTER deadline for booking {}", bookingId);
                // Still process it since money was received
            }

            // Process based on status - "00" means success in PayOS
            if ("00".equals(code)) {
                log.info(">>> PayOS payment SUCCESS - Updating booking status <<<");
                log.info("Before update: status={}, paymentStatus={}",
                        booking.getStatus(), booking.getPaymentStatus());

                // Update payment and booking status
                booking.setPaymentStatus(PaymentStatus.PAID);
                booking.setStatus(BookingStatus.CONFIRMED);

                log.info("After setting: status={}, paymentStatus={}",
                        booking.getStatus(), booking.getPaymentStatus());

                // SAVE THE BOOKING
                Booking savedBooking = bookingRepository.save(booking);
                log.info("Booking SAVED: id={}, status={}, paymentStatus={}",
                        savedBooking.getBookingId(), savedBooking.getStatus(), savedBooking.getPaymentStatus());

                // Update car status to BOOKED and clear reservation time
                Car car = booking.getCar();
                log.info("Updating car {} status from {} to BOOKED", car.getCarId(), car.getStatus());
                car.setStatus(CarStatus.BOOKED);
                car.setReservationExpireTime(null);
                Car savedCar = carRepository.save(car);
                log.info("Car SAVED: id={}, status={}", savedCar.getCarId(), savedCar.getStatus());

                // Send notifications
                try {
                    log.info("Sending notifications...");
                    notificationService.sendPaymentSuccessEmail(booking);
                    notificationService.sendOwnerNotification(booking);
                    log.info("Notifications sent successfully");
                } catch (Exception e) {
                    log.error("Error sending notifications for booking {}: {}", bookingId, e.getMessage());
                    // Don't fail the webhook due to notification failure
                }

                log.info("========================================");
                log.info("=== PAYMENT CONFIRMED SUCCESSFULLY ===");
                log.info("BookingId: {}", bookingId);
                log.info("Booking Status: {}", savedBooking.getStatus());
                log.info("Payment Status: {}", savedBooking.getPaymentStatus());
                log.info("Car Status: {}", savedCar.getStatus());
                log.info("========================================");
                return true;
            } else {
                log.warn("PayOS payment NOT successful for booking {}: code={}", bookingId, code);
                return true; // Acknowledge receipt even for failed payments
            }

        } catch (Exception e) {
            log.error("========================================");
            log.error("=== PAYOS WEBHOOK PROCESSING FAILED ===");
            log.error("Error: {}", e.getMessage(), e);
            log.error("========================================");
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentInfo(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        return PaymentResponseDto.builder()
                .bookingId(bookingId)
                .amount(booking.getHoldingFee())
                .status(booking.getPaymentStatus().name())
                .message("Awaiting PayOS payment")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Override
    public boolean cancelPayOSPayment(Long orderCode) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("cancellationReason", "Booking cancelled by user");

            Map<String, Object> response = payosWebClient.post()
                    .uri("/v2/payment-requests/" + orderCode + "/cancel")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && "00".equals(String.valueOf(response.get("code")))) {
                log.info("Cancelled PayOS payment for orderCode {}", orderCode);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error cancelling PayOS payment for orderCode {}: {}", orderCode, e.getMessage(), e);
            return false;
        }
    }
}
