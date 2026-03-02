package fpt.swp391.carrentalsystem.service.admin.impl;

import fpt.swp391.carrentalsystem.dto.response.OwnerResponse;
import fpt.swp391.carrentalsystem.dto.response.OwnerStatsResponse;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.mapper.admin.OwnerMapper;
import fpt.swp391.carrentalsystem.repository.OwnerRepository;
import fpt.swp391.carrentalsystem.service.admin.ManageOwnerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManageOwnerServiceImpl implements ManageOwnerService {

    // Các field final bắt buộc phải được khởi tạo trong Constructor
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    /**
     * Constructor Injection thủ công
     * Spring sẽ tự động tìm các Bean OwnerRepository và OwnerMapper để truyền vào đây
     */
    public ManageOwnerServiceImpl(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
    }

    @Override
    public Page<OwnerResponse> getAllOwners(String keyword, UserStatus status, Pageable pageable) {
        Page<User> owners;

        // Xử lý keyword: null hoặc rỗng thì gán bằng null để query bỏ qua điều kiện này
        String safeKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        // Kiểm tra xem có đang áp dụng tìm kiếm hoặc bộ lọc không
        if (safeKeyword != null || status != null) {
            // Gọi hàm tìm kiếm và lọc kết hợp trong Repository
            owners = ownerRepository.searchAndFilterOwners(
                    Role.CAR_OWNER,
                    safeKeyword,
                    status,
                    pageable
            );
        } else {
            // Nếu không có tìm kiếm/bộ lọc, lấy toàn bộ danh sách chủ xe theo phân trang
            owners = ownerRepository.findByRole(Role.CAR_OWNER, pageable);
        }

        // Map kết quả sang OwnerResponse
        return owners.map(ownerMapper::toResponse);
    }

    @Override
    public OwnerStatsResponse getOwnerStats() {
        // Trả về DTO thống kê bằng cách đếm trực tiếp từ DB
        return OwnerStatsResponse.builder()
                .totalOwners(ownerRepository.countByRole(Role.CAR_OWNER))
                .activeOwners(ownerRepository.countByRoleAndStatus(Role.CAR_OWNER, UserStatus.ACTIVE))
                .inactiveOwners(ownerRepository.countByRoleAndStatus(Role.CAR_OWNER, UserStatus.DISABLED))
                .pendingOwners(ownerRepository.countByRoleAndStatus(Role.CAR_OWNER, UserStatus.PENDING))
                .build();
    }

    @Override
    public OwnerResponse getOwnerById(Long ownerId) {
        User owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
        return ownerMapper.toResponse(owner);
    }

    @Override
    @Transactional
    public void disableOwner(Long ownerId) {
        updateStatus(ownerId, UserStatus.DISABLED);
    }

    @Override
    @Transactional
    public void enableOwner(Long ownerId) {
        updateStatus(ownerId, UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void approveOwner(Long ownerId) {
        updateStatus(ownerId, UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void setToPending(Long ownerId) {
        updateStatus(ownerId, UserStatus.PENDING);
    }

    /**
     * Helper method dùng chung để cập nhật trạng thái
     */
    private void updateStatus(Long id, UserStatus status) {
        User user = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        user.setStatus(status);
        ownerRepository.save(user);
    }
}