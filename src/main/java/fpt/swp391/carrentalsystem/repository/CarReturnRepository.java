package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Booking;
import fpt.swp391.carrentalsystem.entity.CarReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarReturnRepository extends JpaRepository<CarReturn, Integer> {

    @Query("SELECT cr FROM CarReturn cr " +
            "JOIN FETCH cr.booking b " +
            "JOIN FETCH b.car c " +
            "WHERE cr.returnId = :returnId")
    Optional<CarReturn> findDetailByReturnId(@Param("returnId") Integer returnId);

    @Query("SELECT cr FROM CarReturn cr " +
            "JOIN FETCH cr.booking b " +
            "WHERE b.customer.id = :customerId")
    List<CarReturn> findReturnsByCustomerId(@Param("customerId") Long customerId);


    // lấy danh sách booking đã hoàn thành nhưng chưa tạo car return
    @Query("SELECT b FROM Booking b " +
            "WHERE b.customer.id = :userId " +
            "AND b.status = 'COMPLETED' " +
            "AND b.bookingId NOT IN (SELECT cr.booking.bookingId FROM CarReturn cr)")
    List<Booking> findReadyToReturn(@Param("userId") Long userId);

    //lay du cac carReturn cua 1 owner
    @Query("SELECT cr FROM CarReturn cr " +
            "WHERE cr.booking.bookingId IN (" +
            "  SELECT b.bookingId FROM Booking b " +
            "  WHERE b.car.owner.id = :ownerId" +
            ")")
    List<CarReturn> findByOwnerId(@Param("ownerId") Integer ownerId);
}
