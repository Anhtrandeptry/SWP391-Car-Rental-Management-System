package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<User, Long> {

    // Lấy toàn bộ customer
    List<User> findByRole(Role role);
}
