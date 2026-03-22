package fpt.swp391.carrentalsystem.dto.request;


import fpt.swp391.carrentalsystem.entity.CarDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDocumentDTO {

    private Long id;
    private Long carId;
    private Long ownerId;
    private String documentType;
    private String documentTypeName;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private LocalDate expiryDate;
    private String status;
    private LocalDateTime uploadedAt;
    private boolean required;

    // Constructor từ Entity
    public CarDocumentDTO(CarDocument document) {
        this.id = document.getId();
        this.carId = document.getCarId();
        this.ownerId = document.getOwnerId();
        this.documentType = document.getDocumentType().name();
        this.documentTypeName = document.getDocumentType().getDisplayName();
        this.fileName = document.getFileName();
        this.filePath = document.getFilePath();
        this.fileSize = document.getFileSize();
        this.fileType = document.getFileType();
        this.expiryDate = document.getExpiryDate();
        this.status = document.getStatus().name();
        this.uploadedAt = document.getUploadedAt();
        this.required = document.getDocumentType().isRequired();
    }
}