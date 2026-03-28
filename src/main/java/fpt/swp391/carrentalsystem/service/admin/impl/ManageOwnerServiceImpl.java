package fpt.swp391.carrentalsystem.service.admin.impl;

import fpt.swp391.carrentalsystem.dto.response.BookingHistoryResponse;
import fpt.swp391.carrentalsystem.dto.response.OwnerResponse;
import fpt.swp391.carrentalsystem.dto.response.OwnerStatsResponse;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import fpt.swp391.carrentalsystem.mapper.admin.OwnerMapper;
import fpt.swp391.carrentalsystem.repository.BookingRepository;
import fpt.swp391.carrentalsystem.repository.OwnerRepository;
import fpt.swp391.carrentalsystem.service.admin.ManageOwnerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ManageOwnerServiceImpl implements ManageOwnerService {

    // Các field final bắt buộc phải được khởi tạo trong Constructor
    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;
    private final BookingRepository bookingRepository;

    /**
     * Constructor Injection thủ công
     * Spring sẽ tự động tìm các Bean OwnerRepository và OwnerMapper để truyền vào đây
     */
    public ManageOwnerServiceImpl(OwnerRepository ownerRepository,
                                  OwnerMapper ownerMapper,
                                  BookingRepository bookingRepository) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
        this.bookingRepository = bookingRepository;
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
        // 🔥 lấy số xe đã được thuê (DISTINCT)
        Map<Long, Long> rentedCarMap = bookingRepository.countRentedCarsByOwner()
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (Long) r[1]
                ));

// 🔥 lấy doanh thu
        Map<Long, BigDecimal> revenueMap = bookingRepository.getRevenueByOwner()
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> (BigDecimal) r[1]
                ));

// 🔥 map + inject thêm data
        return owners.map(owner -> {
            OwnerResponse res = ownerMapper.toResponse(owner);

            Long ownerId = owner.getId();

            res.setNumberOfCars(
                    rentedCarMap.getOrDefault(ownerId, 0L).intValue()
            );

            res.setTotalRevenue(
                    revenueMap.getOrDefault(ownerId, BigDecimal.ZERO).doubleValue()
            );

            return res;
        });
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

        OwnerResponse res = ownerMapper.toResponse(owner);

        // 🔥 số lượt thuê
        Long totalBookings = bookingRepository.countBookingsByOwner(ownerId);

        // 🔥 doanh thu
        BigDecimal totalRevenue = bookingRepository.sumRevenueByOwner(ownerId);

        res.setNumberOfCars(totalBookings != null ? totalBookings.intValue() : 0);
        res.setTotalRevenue(totalRevenue != null ? totalRevenue.doubleValue() : 0);

        return res;
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

    @Override
    public List<BookingHistoryResponse> getRecentBookingsByOwner(Long ownerId) {

        return bookingRepository
                .findTopOwnerBookingsWithDetails(ownerId, Pageable.ofSize(5))
                .stream()
                .map(b -> BookingHistoryResponse.builder()
                        .id(Long.valueOf(b.getBookingId()))
                        .carName(b.getCar().getName())
                        .startDate(b.getStartDate())
                        .endDate(b.getEndDate())
                        .totalAmount(
                                b.getTotalAmount() != null
                                        ? b.getTotalAmount().doubleValue()
                                        : 0
                        )
                        .status(b.getStatus().name())
                        .build())
                .toList();
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