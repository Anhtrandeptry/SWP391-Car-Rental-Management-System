package fpt.swp391.carrentalsystem.service;


import fpt.swp391.carrentalsystem.dto.request.CarDocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface CarDocumentService {

    /**
     * Upload document
     */
    CarDocumentDTO uploadDocument(
            MultipartFile file,
            Long ownerId,
            String documentType,
            LocalDate expiryDate
    );

    /**
     * Lấy danh sách documents của owner (chưa gắn car)
     */
    List<CarDocumentDTO> getOwnerDocuments(Long ownerId);

    /**
     * Xóa document
     */
    void deleteDocument(Long documentId, Long ownerId);

    /**
     * Kiểm tra đã upload đủ giấy tờ bắt buộc chưa
     */
    boolean hasAllRequiredDocuments(Long ownerId);

    /**
     * Gắn documents vào car sau khi tạo xe
     */
    void attachDocumentsToCar(Long ownerId, Long carId);
}
