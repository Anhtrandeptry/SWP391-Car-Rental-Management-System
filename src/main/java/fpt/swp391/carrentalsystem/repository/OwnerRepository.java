package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.User; // Đảm bảo import đúng Entity này
import fpt.swp391.carrentalsystem.enums.Role;
import fpt.swp391.carrentalsystem.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerRepository extends JpaRepository<User, Long> {

    // Tìm danh sách theo Role
    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role " +
            "AND (:status IS NULL OR u.status = :status) " + // Lọc theo trạng thái
            "AND (:keyword IS NULL OR " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "u.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchAndFilterOwners(@Param("role") Role role,
                                     @Param("keyword") String keyword,
                                     @Param("status") UserStatus status,
                                     Pageable pageable);

    // Sử dụng count trực tiếp từ DB sẽ tối ưu hơn so với dùng Stream.size()
    long countByRole(Role role);

    long countByRoleAndStatus(Role role, UserStatus status);
}