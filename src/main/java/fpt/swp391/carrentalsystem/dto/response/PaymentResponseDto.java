package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {
    private String transactionId;
    private Integer bookingId;
    private BigDecimal amount;
    private String status;
    private String paymentUrl;
    private String qrCode;
    private String message;
    private Long timestamp;
}

