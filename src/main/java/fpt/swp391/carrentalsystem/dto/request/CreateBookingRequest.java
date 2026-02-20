package fpt.swp391.carrentalsystem.dto.request;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {
    @NotNull(message = "Car ID is required")
    private Integer carId;
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the future")
    private LocalDateTime startDate;
    @NotNull(message = "End date is required")
    @Future(message = "End date must be after start date")
    private LocalDateTime endDate;
    private String pickupLocation;
}
