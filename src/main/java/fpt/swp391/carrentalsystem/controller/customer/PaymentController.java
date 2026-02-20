package fpt.swp391.carrentalsystem.controller.customer;

import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import fpt.swp391.carrentalsystem.service.PaymentService;
import fpt.swp391.carrentalsystem.service.BookingService;
import fpt.swp391.carrentalsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final NotificationService notificationService;

    @GetMapping("/return")
    public String handlePaymentReturn(@RequestParam String vnp_ResponseCode,
                                     @RequestParam String vnp_TxnRef,
                                     @RequestParam String vnp_Amount,
                                     Model model) {
        try {
            PaymentResponseDto response = paymentService.verifyPayment(
                    String.format("vnp_ResponseCode=%s&vnp_TxnRef=%s&vnp_Amount=%s",
                            vnp_ResponseCode, vnp_TxnRef, vnp_Amount)
            );

            if ("SUCCESS".equals(response.getStatus())) {
                // Extract booking ID from transaction reference
                String[] parts = vnp_TxnRef.split("_");
                if (parts.length >= 2) {
                    Integer bookingId = Integer.parseInt(parts[1]);
                    bookingService.confirmPayment(bookingId);

                    model.addAttribute("success", "Thanh toán thành công!");
                    model.addAttribute("bookingId", bookingId);
                    return "customer/booking-success";
                }
            }

            model.addAttribute("error", "Thanh toán thất bại. Vui lòng thử lại.");
            return "customer/payment-error";

        } catch (Exception e) {
            log.error("Error handling payment return: {}", e.getMessage(), e);
            model.addAttribute("error", "Có lỗi xảy ra. Vui lòng liên hệ hỗ trợ.");
            return "customer/payment-error";
        }
    }

    @PostMapping("/webhook")
    @ResponseBody
    public String handlePaymentWebhook(@RequestBody String webhookData) {
        try {
            PaymentResponseDto response = paymentService.processPaymentWebhook(webhookData);
            log.info("Webhook processed: {}", response.getStatus());
            return "OK";
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return "ERROR";
        }
    }

    @GetMapping("/{bookingId}/qr")
    @ResponseBody
    public PaymentResponseDto getQRCode(@PathVariable Integer bookingId) {
        try {
            // Get booking details from service
            // Generate QR code
            String qrCode = paymentService.generateQRCode(bookingId, java.math.BigDecimal.valueOf(500000));

            return PaymentResponseDto.builder()
                    .bookingId(bookingId)
                    .qrCode(qrCode)
                    .status("GENERATED")
                    .message("QR code generated successfully")
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("Error generating QR code: {}", e.getMessage(), e);
            return PaymentResponseDto.builder()
                    .status("ERROR")
                    .message("Error generating QR code: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }
}

