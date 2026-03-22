package fpt.swp391.carrentalsystem.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerStatsResponse {
    private long totalOwners;      // Tổng số chủ xe
    private long activeOwners;     // Đang hoạt động (Màu xanh lá)
    private long inactiveOwners;   // Vô hiệu hóa (Màu đỏ)
    private long pendingOwners;    // Chờ phê duyệt (Màu cam)

    // Nếu bạn muốn hiển thị % tăng trưởng như trong ảnh, có thể thêm các trường sau:
    private double totalGrowthPercent;
    private double activeGrowthPercent;
    private double inactiveGrowthPercent;
    private double pendingGrowthPercent;
}