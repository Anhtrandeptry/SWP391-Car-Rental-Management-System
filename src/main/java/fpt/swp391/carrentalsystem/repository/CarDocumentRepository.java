package fpt.swp391.carrentalsystem.repository;


import fpt.swp391.carrentalsystem.entity.CarDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarDocumentRepository extends JpaRepository<CarDocument, Long> {

    /**
     * Tìm documents theo car ID
     */
    List<CarDocument> findByCarId(Long carId);

    /**
     * Tìm documents theo owner ID
     */
    List<CarDocument> findByOwnerId(Long ownerId);

    /**
     * Tìm document theo car ID và loại
     */
    Optional<CarDocument> findByCarIdAndDocumentType(Long carId, CarDocument.DocumentType documentType);

    /**
     * Tìm document theo owner ID và loại (khi chưa có car)
     */
    Optional<CarDocument> findByOwnerIdAndDocumentTypeAndCarIdIsNull(
            Long ownerId,
            CarDocument.DocumentType documentType
    );

    /**
     * Đếm số document theo car ID
     */
    long countByCarId(Long carId);

    /**
     * Đếm số document bắt buộc theo car ID
     */
    @Query("SELECT COUNT(d) FROM CarDocument d WHERE d.carId = :carId " +
            "AND d.documentType IN ('REGISTRATION', 'INSURANCE', 'OWNER_LICENSE', 'OWNER_ID')")
    long countRequiredDocumentsByCarId(@Param("carId") Long carId);

    /**
     * Kiểm tra đã upload đủ giấy tờ bắt buộc chưa
     */
    @Query("SELECT CASE WHEN COUNT(d) >= 4 THEN true ELSE false END " +
            "FROM CarDocument d WHERE d.ownerId = :ownerId AND d.carId IS NULL " +
            "AND d.documentType IN ('REGISTRATION', 'INSURANCE', 'OWNER_LICENSE', 'OWNER_ID')")
    boolean hasAllRequiredDocuments(@Param("ownerId") Long ownerId);

    /**
     * Xóa documents tạm của owner (chưa gắn car)
     */
    void deleteByOwnerIdAndCarIdIsNull(Long ownerId);

    /**
     * Lấy documents tạm của owner
     */
    List<CarDocument> findByOwnerIdAndCarIdIsNull(Long ownerId);
}