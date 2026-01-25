package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phone);

    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);
}

