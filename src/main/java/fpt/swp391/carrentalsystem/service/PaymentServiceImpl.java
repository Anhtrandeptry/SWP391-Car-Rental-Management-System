package fpt.swp391.carrentalsystem.service;

import fpt.swp391.carrentalsystem.dto.response.PaymentResponseDto;
import com.google.gson.Gson;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${vnpay.tmn-code:GIANG2025}")
    private String tmnCode;

    @Value("${vnpay.hash-secret:YOUR_HASH_SECRET}")
    private String hashSecret;

    @Value("${vnpay.payment-url:https://sandbox.vnpayment.vn/paygate}")
    private String paymentUrl;

    @Value("${vnpay.return-url:http://localhost:8080/payment/return}")
    private String returnUrl;

    private final Gson gson = new Gson();

    @Override
    public PaymentResponseDto createPaymentRequest(Integer bookingId, BigDecimal amount, String description) {
        try {
            long vnpAmount = amount.longValue() * 100;
            String vnpTxnRef = "BOOKING_" + bookingId + "_" + System.currentTimeMillis();

            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", vnpTxnRef);
            vnpParams.put("vnp_OrderInfo", description);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", returnUrl);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnpCreateDate = formatter.format(new Date());
            vnpParams.put("vnp_CreateDate", vnpCreateDate);

            String queryUrl = buildQueryUrl(vnpParams);
            String secureHash = generateSecureHash(queryUrl);

            String paymentLink = paymentUrl + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;

            log.info("Payment request created for booking: {}, Amount: {}", bookingId, amount);

            return PaymentResponseDto.builder()
                    .transactionId(vnpTxnRef)
                    .bookingId(bookingId)
                    .amount(amount)
                    .status("PENDING")
                    .paymentUrl(paymentLink)
                    .message("Payment link generated successfully")
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("Error creating payment request: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating payment request: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponseDto verifyPayment(String vnpayResponse) {
        try {
            // Parse VnPay response
            Map<String, String> responseParams = new HashMap<>();
            String[] params = vnpayResponse.split("&");

            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    responseParams.put(keyValue[0], URLEncoder.encode(keyValue[1], StandardCharsets.UTF_8.toString()));
                }
            }

            String vnpSecureHash = responseParams.get("vnp_SecureHash");
            responseParams.remove("vnp_SecureHash");

            String queryString = buildQueryUrl(responseParams);
            String computeHash = generateSecureHash(queryString);

            if (computeHash.equals(vnpSecureHash)) {
                String responseCode = responseParams.get("vnp_ResponseCode");

                if ("00".equals(responseCode)) {
                    log.info("Payment verified successfully. Transaction: {}", responseParams.get("vnp_TxnRef"));
                    return PaymentResponseDto.builder()
                            .transactionId(responseParams.get("vnp_TxnRef"))
                            .status("SUCCESS")
                            .message("Payment verified successfully")
                            .timestamp(System.currentTimeMillis())
                            .build();
                } else {
                    return PaymentResponseDto.builder()
                            .transactionId(responseParams.get("vnp_TxnRef"))
                            .status("FAILED")
                            .message("Payment failed with response code: " + responseCode)
                            .timestamp(System.currentTimeMillis())
                            .build();
                }
            } else {
                log.warn("Invalid secure hash. Expected: {}, Got: {}", computeHash, vnpSecureHash);
                return PaymentResponseDto.builder()
                        .status("INVALID")
                        .message("Invalid secure hash")
                        .timestamp(System.currentTimeMillis())
                        .build();
            }

        } catch (Exception e) {
            log.error("Error verifying payment: {}", e.getMessage(), e);
            return PaymentResponseDto.builder()
                    .status("ERROR")
                    .message("Error verifying payment: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    @Override
    public PaymentResponseDto processPaymentWebhook(String webhookData) {
        try {
            log.info("Processing webhook data: {}", webhookData);

            // Webhook processing logic here
            // Verify webhook signature
            // Update booking status based on payment status

            return PaymentResponseDto.builder()
                    .status("SUCCESS")
                    .message("Webhook processed successfully")
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return PaymentResponseDto.builder()
                    .status("ERROR")
                    .message("Error processing webhook: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();
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

    private String buildQueryUrl(Map<String, String> params) throws Exception {
        StringBuilder query = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                query.append("&");
            }
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
        }

        return query.toString();
    }

    private String generateSecureHash(String queryUrl) throws Exception {
        String hashInput = hashSecret + queryUrl;
        return hmacSHA512(hashSecret, queryUrl);
    }

    private String hmacSHA512(String key, String data) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
        javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                0,
                key.getBytes(StandardCharsets.UTF_8).length,
                "HmacSHA512"
        );
        mac.init(secretKeySpec);
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

