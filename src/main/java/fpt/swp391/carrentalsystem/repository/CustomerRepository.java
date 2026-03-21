package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<User, Long> {

    // Lấy toàn bộ customer
    Page<User> findByRole(Role role, Pageable pageable);

    // Hàm tìm kiếm theo keyword (Tên, Email, SĐT) kết hợp với Role
    @Query("SELECT u FROM User u WHERE u.role = :role " +
            "AND (:status IS NULL OR u.status = :status) " + // Lọc theo trạng thái nếu có
            "AND (:keyword IS NULL OR " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "u.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchAndFilterCustomers(@Param("role") Role role,
                                        @Param("keyword") String keyword,
                                        @Param("status") UserStatus status,
                                        Pageable pageable);

    // Đếm tổng số lượng theo Role
    long countByRole(Role role);

    // Đếm số lượng theo Role và Trạng thái cụ thể
    long countByRoleAndStatus(Role role, UserStatus status);


}
