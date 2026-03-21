package fpt.swp391.carrentalsystem.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO for car search request
 * User must select location and rental period first before seeing available cars
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSearchRequest {

    @NotBlank(message = "Vui lòng chọn địa điểm thuê xe")
    private String location;

    @NotNull(message = "Vui lòng chọn ngày giờ bắt đầu")
    private LocalDateTime startDate;

    @NotNull(message = "Vui lòng chọn ngày giờ kết thúc")
    private LocalDateTime endDate;
}

