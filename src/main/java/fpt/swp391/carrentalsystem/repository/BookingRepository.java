package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByCar_Owner_IdAndStatus(Integer ownerId, BookingStatus status);


    List<Booking> findByCustomerAndStatusIn(User customer, List<BookingStatus> statuses);

}