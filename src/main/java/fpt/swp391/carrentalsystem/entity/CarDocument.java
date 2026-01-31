package fpt.swp391.carrentalsystem.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DocumentStatus status = DocumentStatus.PENDING;

    @CreationTimestamp
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // ENUMS
    public enum DocumentType {
        REGISTRATION("Đăng ký xe"),
        INSURANCE("Bảo hiểm xe"),
        INSPECTION_CERTIFICATE("Giấy kiểm định"),
        OWNER_LICENSE("Bằng lái chủ xe"),
        OWNER_ID("CMND/CCCD chủ xe"),
        OWNERSHIP_CERTIFICATE("Giấy sở hữu"),
        OTHER("Giấy tờ khác");

        private final String displayName;

        DocumentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DocumentStatus {
        PENDING("Chờ duyệt"),
        APPROVED("Đã duyệt"),
        REJECTED("Từ chối");

        private final String displayName;

        DocumentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
