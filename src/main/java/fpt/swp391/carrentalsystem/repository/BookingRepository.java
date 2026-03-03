package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByCustomerOrderByCreatedAtDesc(User customer);

    @Query("SELECT b FROM Booking b WHERE b.car.owner = :owner ORDER BY b.createdAt DESC")
    List<Booking> findByCarOwner(@Param("owner") User owner);
}