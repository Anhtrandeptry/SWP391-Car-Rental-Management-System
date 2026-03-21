package fpt.swp391.carrentalsystem.repository;

import fpt.swp391.carrentalsystem.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
// Lấy danh sách feedback của một customer, sắp xếp theo createdAt giảm dần.
    List<Feedback> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    boolean existsByBookingIdAndCustomer_Id(Long bookingId, Long customerId);

    long countByCustomer_Id(Long customerId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.customer.id = :customerId")
    Double avgRatingByCustomer(@Param("customerId") Long customerId);

    @Query("""
           SELECT COUNT(f) 
           FROM Feedback f
           WHERE f.customer.id = :customerId
             AND f.systemReply IS NOT NULL
             AND TRIM(f.systemReply) <> ''
           """)
    long countRepliedByCustomer(@Param("customerId") Long customerId);
}