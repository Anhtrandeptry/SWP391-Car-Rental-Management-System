package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;

    // VietQR configuration - can be moved to application.properties
    @Value("${vietqr.bank-id:970415}")
    private String bankId; // VietinBank as default

    @Value("${vietqr.account-number:1234567890}")
    private String accountNumber;

    @Value("${vietqr.account-name:CAR RENTAL SYSTEM}")
    private String accountName;

    @Value("${vietqr.template:compact2}")
    private String template;

    @Override
    public String generateVietQRUrl(Integer bookingId, BigDecimal amount, String description) {
        try {
            String encodedAccountName = URLEncoder.encode(accountName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
            String encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");

            // VietQR format: https://img.vietqr.io/image/{BANK_ID}-{ACCOUNT_NO}-{TEMPLATE}.png
            String url = String.format(
                    "https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s&accountName=%s",
                    bankId,
                    accountNumber,
                    template,
                    amount.longValue(),
                    encodedDescription,
                    encodedAccountName
            );

            log.info("Generated VietQR URL for booking {}: {}", bookingId, url);
            return url;

        } catch (Exception e) {
            log.error("Error generating VietQR URL: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating VietQR URL: " + e.getMessage());
        }
    }

    @Override
    public String generateQRCode(Integer bookingId, BigDecimal amount) throws Exception {
        String qrContent = String.format("BOOKING_%d_%.0f", bookingId, amount.doubleValue());

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        byte[] pngData = pngOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(pngData);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentInfo(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        String description = "BOOKING" + bookingId;
        String qrUrl = generateVietQRUrl(bookingId, booking.getHoldingFee(), description);

        return PaymentResponseDto.builder()
                .bookingId(bookingId)
                .amount(booking.getHoldingFee())
                .status("PENDING")
                .paymentUrl(qrUrl)
                .message("Please scan the QR code to complete payment")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
