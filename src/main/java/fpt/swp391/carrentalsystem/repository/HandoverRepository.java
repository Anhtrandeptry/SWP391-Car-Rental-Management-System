package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Handover;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HandoverRepository extends JpaRepository<Handover, Integer> {

    Optional<Handover> findByBooking_BookingId(Integer bookingId);
}