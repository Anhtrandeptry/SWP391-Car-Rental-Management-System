package fpt.swp391.carrentalsystem.service.admin;

import fpt.swp391.carrentalsystem.dto.response.OwnerResponse;
import fpt.swp391.carrentalsystem.dto.response.OwnerStatsResponse; // Giả định bạn có DTO stats tương tự Customer
import fpt.swp391.carrentalsystem.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManageOwnerService {

    Page<OwnerResponse> getAllOwners(String keyword, UserStatus status, Pageable pageable);

    // Hàm tính toán thống kê số lượng các tài khoản owner (Tổng, Active, Inactive, Pending)
    OwnerStatsResponse getOwnerStats();

    OwnerResponse getOwnerById(Long ownerId);

    void disableOwner(Long ownerId);

    void enableOwner(Long ownerId);

    // Hàm để chuyển từ Pending sang Active (Phê duyệt chủ xe mới)
    void approveOwner(Long ownerId);

    // Hàm để chuyển bất kỳ trạng thái nào về Pending
    void setToPending(Long ownerId);
}