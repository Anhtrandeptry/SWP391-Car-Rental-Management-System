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
            log.info("PayOS return received - code: {}, id: {}, cancel: {}, status: {}, orderCode: {}",
                    code, id, cancel, status, orderCode);

            // Extract bookingId from orderCode (orderCode = bookingId * 10000 + suffix)
            Integer bookingId = null;
            if (orderCode != null) {
                bookingId = (int) (orderCode / 10000);
                log.info("Extracted bookingId {} from orderCode {}", bookingId, orderCode);
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
                log.info("PayOS payment successful for orderCode: {}, bookingId: {}", orderCode, bookingId);

                // Confirm payment if not already confirmed by webhook
                if (bookingId != null) {
                    try {
                        bookingService.confirmPayment(bookingId);
                    } catch (Exception e) {
                        // May already be confirmed by webhook, that's ok
                        log.info("Payment may already be confirmed: {}", e.getMessage());
                    }
                }

                model.addAttribute("success", "Thanh toán thành công!");
                model.addAttribute("bookingId", bookingId);
                return "customer/booking-success";
            } else {
                log.warn("PayOS payment not successful - code: {}, status: {}", code, status);
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
            // Extract bookingId from orderCode (orderCode = bookingId * 10000 + suffix)
            Integer bookingId = (int) (orderCode / 10000);
            model.addAttribute("bookingId", bookingId);
        }
        return "customer/payment-error";
    }

    /**
     * PayOS Webhook - Called by PayOS server to notify payment status
     * This endpoint must be publicly accessible
     */
    @PostMapping("/payos-webhook")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payosWebhook(@RequestBody Map<String, Object> webhookData) {
        try {
            log.info("PayOS webhook received: {}", webhookData);

            boolean processed = paymentService.processPayOSWebhook(webhookData);

            if (processed) {
                log.info("PayOS webhook processed successfully");
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                log.warn("PayOS webhook processing failed");
                return ResponseEntity.ok(Map.of("success", false, "message", "Processing failed"));
            }

        } catch (Exception e) {
            log.error("Error processing PayOS webhook: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
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
}
