package fpt.swp391.carrentalsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long disabledUsers;
    private long pendingApproval;

    // Bạn có thể thêm các trường tăng trưởng MoM nếu muốn tính toán sau này
    private String totalGrowth = "+12%";
    private String activeGrowth = "+8%";
}