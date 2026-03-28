package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryResponse {
    private Long id;
    private String carName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalAmount;
    private String status;
}
