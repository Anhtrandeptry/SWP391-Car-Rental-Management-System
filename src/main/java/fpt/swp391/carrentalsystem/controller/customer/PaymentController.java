package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import fpt.swp391.carrentalsystem.service.BookingService;
import fpt.swp391.carrentalsystem.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    /**
     * PayOS Return URL - User is redirected here after completing/cancelling payment on PayOS
     */
    @GetMapping("/payos-return")
    public String payosReturn(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String cancel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long orderCode,
            Model model) {
        try {
            log.info("╔══════════════════════════════════════════════════════════════╗");
            log.info("║           PAYOS RETURN URL HIT                                ║");
            log.info("╚══════════════════════════════════════════════════════════════╝");
            log.info("code: {}, id: {}, cancel: {}, status: {}, orderCode: {}",
                    code, id, cancel, status, orderCode);

            // IMPORTANT: orderCode is NOT the same as bookingId anymore!
            // We need to find the booking by orderCode
            Integer bookingId = null;
            if (orderCode != null) {
                // Try to find booking by orderCode first
                try {
                    PaymentResponseDto paymentInfo = paymentService.getPaymentInfoByOrderCode(orderCode);
                    if (paymentInfo != null) {
                        bookingId = paymentInfo.getBookingId();
                        log.info("Found bookingId {} for orderCode {}", bookingId, orderCode);
                    }
                } catch (Exception e) {
                    log.warn("Could not find booking by orderCode {}: {}", orderCode, e.getMessage());
                    // Fallback: try treating orderCode as bookingId (for legacy bookings)
                    bookingId = orderCode.intValue();
                    log.info("Fallback: using orderCode as bookingId: {}", bookingId);
                }
            }

            // Check if payment was cancelled
            if ("true".equals(cancel)) {
                log.info("PayOS payment cancelled for orderCode: {}", orderCode);
                model.addAttribute("error", "Thanh toán đã bị hủy.");
                if (bookingId != null) {
                    model.addAttribute("bookingId", bookingId);
                }
                return "customer/payment-error";
            }

            // Check payment status - "00" means success in PayOS
            if ("00".equals(code) || "PAID".equals(status)) {
                log.info("PayOS payment SUCCESS for orderCode: {}, bookingId: {}", orderCode, bookingId);

                // Confirm payment if not already confirmed by webhook
                if (bookingId != null) {
                    try {
                        bookingService.confirmPayment(bookingId);
                        log.info("Payment confirmed via return URL for booking {}", bookingId);
                    } catch (Exception e) {
                        // May already be confirmed by webhook, that's ok
                        log.info("Payment confirmation result: {}", e.getMessage());
                    }
                }

                model.addAttribute("success", "Thanh toán thành công!");
                model.addAttribute("bookingId", bookingId);
                return "customer/booking-success";
            } else {
                log.warn("PayOS payment NOT successful - code: {}, status: {}", code, status);
                model.addAttribute("error", "Thanh toán thất bại. Mã lỗi: " + code);
                if (bookingId != null) {
                    model.addAttribute("bookingId", bookingId);
                }
                return "customer/payment-error";
            }

        } catch (Exception e) {
            log.error("Error processing PayOS return: {}", e.getMessage(), e);
            model.addAttribute("error", "Có lỗi xảy ra khi xử lý thanh toán. Vui lòng liên hệ hỗ trợ.");
            return "customer/payment-error";
        }
    }

    /**
     * PayOS Cancel URL - User is redirected here when cancelling payment
     */
    @GetMapping("/payos-cancel")
    public String payosCancel(
            @RequestParam(required = false) Long orderCode,
            Model model) {
        log.info("PayOS payment cancelled for orderCode: {}", orderCode);
        model.addAttribute("error", "Bạn đã hủy thanh toán.");
        if (orderCode != null) {
            // IMPORTANT: orderCode IS the bookingId directly
            Integer bookingId = orderCode.intValue();
            model.addAttribute("bookingId", bookingId);
        }
        return "customer/payment-error";
    }

    /**
     * PayOS Webhook - Called by PayOS server to notify payment status
     * This endpoint must be publicly accessible (permitAll in SecurityConfig)
     *
     * IMPORTANT: Always return 200 OK to prevent PayOS from retrying
     */
    @PostMapping("/payos-webhook")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payosWebhook(@RequestBody Map<String, Object> webhookData) {
        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║           PAYOS WEBHOOK RECEIVED                              ║");
        log.info("╚══════════════════════════════════════════════════════════════╝");
        log.info("Timestamp: {}", java.time.LocalDateTime.now());
        log.info("Raw webhook data: {}", webhookData);

        // Log individual fields for debugging
        if (webhookData != null) {
            log.info("Webhook keys: {}", webhookData.keySet());
            log.info("code: {}", webhookData.get("code"));
            log.info("desc: {}", webhookData.get("desc"));
            log.info("signature: {}", webhookData.get("signature"));

            Object dataObj = webhookData.get("data");
            if (dataObj != null) {
                log.info("data object type: {}", dataObj.getClass().getName());
                log.info("data content: {}", dataObj);
            } else {
                log.warn("data field is NULL!");
            }
        } else {
            log.error("Webhook data is completely NULL!");
        }

        try {
            boolean processed = paymentService.processPayOSWebhook(webhookData);

            if (processed) {
                log.info("╔══════════════════════════════════════════════════════════════╗");
                log.info("║           WEBHOOK PROCESSED SUCCESSFULLY                      ║");
                log.info("╚══════════════════════════════════════════════════════════════╝");
            } else {
                log.warn("╔══════════════════════════════════════════════════════════════╗");
                log.warn("║           WEBHOOK PROCESSING RETURNED FALSE                   ║");
                log.warn("╚══════════════════════════════════════════════════════════════╝");
            }

            // ALWAYS return 200 OK to prevent PayOS from retrying
            return ResponseEntity.ok(Map.of(
                "success", processed,
                "message", processed ? "Webhook processed" : "Processing failed but acknowledged"
            ));

        } catch (Exception e) {
            log.error("╔══════════════════════════════════════════════════════════════╗");
            log.error("║           WEBHOOK PROCESSING EXCEPTION                        ║");
            log.error("╚══════════════════════════════════════════════════════════════╝");
            log.error("Error: {}", e.getMessage(), e);

            // ALWAYS return 200 OK even on error to prevent PayOS retry loops
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Error: " + e.getMessage(),
                "acknowledged", true
            ));
        }
    }

    @GetMapping("/success/{bookingId}")
    public String paymentSuccess(@PathVariable Integer bookingId, Model model) {
        try {
            model.addAttribute("bookingId", bookingId);
            model.addAttribute("success", "Thanh toán thành công!");
            return "customer/booking-success";
        } catch (Exception e) {
            log.error("Error loading success page: {}", e.getMessage(), e);
            model.addAttribute("error", "Có lỗi xảy ra.");
            return "customer/payment-error";
        }
    }

    @GetMapping("/error")
    public String paymentError(Model model) {
        model.addAttribute("error", "Thanh toán thất bại. Vui lòng thử lại.");
        return "customer/payment-error";
    }

    /**
     * API endpoint to check payment status (for AJAX polling)
     */
    @GetMapping("/api/status/{bookingId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPaymentStatus(@PathVariable Integer bookingId) {
        try {
            PaymentResponseDto paymentInfo = paymentService.getPaymentInfo(bookingId);
            Map<String, Object> response = new HashMap<>();
            response.put("bookingId", bookingId);
            response.put("status", paymentInfo.getStatus());
            response.put("paid", "PAID".equals(paymentInfo.getStatus()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking payment status for booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.ok(Map.of("error", e.getMessage(), "paid", false));
        }
    }

    /**
     * TEST ENDPOINT: Manually simulate webhook for testing (REMOVE IN PRODUCTION)
     * Usage: POST /payment/test-webhook?orderCode=123456789
     */
    @PostMapping("/test-webhook")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testWebhook(@RequestParam Long orderCode) {
        log.warn("╔══════════════════════════════════════════════════════════════╗");
        log.warn("║           TEST WEBHOOK TRIGGERED (DEBUG ONLY)                 ║");
        log.warn("╚══════════════════════════════════════════════════════════════╝");
        log.warn("OrderCode: {}", orderCode);

        // Simulate PayOS webhook data structure
        Map<String, Object> simulatedData = new HashMap<>();
        simulatedData.put("orderCode", orderCode);
        simulatedData.put("amount", 500000);
        simulatedData.put("description", "Test payment");
        simulatedData.put("accountNumber", "TEST");
        simulatedData.put("reference", "TEST-REF");
        simulatedData.put("transactionDateTime", java.time.LocalDateTime.now().toString());
        simulatedData.put("paymentLinkId", "TEST-LINK-ID");

        Map<String, Object> simulatedWebhook = new HashMap<>();
        simulatedWebhook.put("code", "00"); // Success code
        simulatedWebhook.put("desc", "success");
        simulatedWebhook.put("data", simulatedData);
        simulatedWebhook.put("signature", "test-signature-bypassed");

        try {
            boolean processed = paymentService.processPayOSWebhook(simulatedWebhook);
            return ResponseEntity.ok(Map.of(
                "success", processed,
                "message", processed ? "Test webhook processed successfully" : "Test webhook processing failed",
                "orderCode", orderCode
            ));
        } catch (Exception e) {
            log.error("Test webhook error: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage(),
                "orderCode", orderCode
            ));
        }
    }

    /**
     * TEST ENDPOINT: Check if webhook endpoint is accessible
     */
    @GetMapping("/webhook-health")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> webhookHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Webhook endpoint is accessible",
            "timestamp", java.time.LocalDateTime.now().toString(),
            "endpoint", "/payment/payos-webhook"
        ));
    }
}

