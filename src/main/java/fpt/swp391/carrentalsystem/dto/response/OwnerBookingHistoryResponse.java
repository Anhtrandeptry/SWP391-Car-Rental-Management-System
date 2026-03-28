package fpt.swp391.carrentalsystem.dto.response;

import java.time.LocalDateTime;

public class OwnerBookingHistoryResponse {
    private Long id;
    private String carName;
    private String customerName; // 🔥 thêm
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalAmount;
    private String status;
}
