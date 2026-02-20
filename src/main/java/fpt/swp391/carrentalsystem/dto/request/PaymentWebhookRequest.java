package fpt.swp391.carrentalsystem.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentWebhookRequest {
    private String vnp_TxnRef;
    private String vnp_Amount;
    private String vnp_ResponseCode;
    private String vnp_BankCode;
    private String vnp_CardType;
    private String vnp_PayDate;
    private String vnp_SecureHash;
}

