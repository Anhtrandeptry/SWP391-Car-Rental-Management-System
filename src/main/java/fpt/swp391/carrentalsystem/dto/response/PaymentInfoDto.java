package fpt.swp391.carrentalsystem.dto.response;
import lombok.*;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfoDto {
    private Integer bookingId;
    private BigDecimal holdingFee;
    private BigDecimal depositAmount;
    private BigDecimal rentalFee;
    private BigDecimal totalAmount;
    private String qrCodeUrl;
}
