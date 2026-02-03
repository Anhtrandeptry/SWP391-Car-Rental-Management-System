package fpt.swp391.carrentalsystem.service;



import fpt.swp391.carrentalsystem.dto.request.CarDocumentDTO;
import fpt.swp391.carrentalsystem.entity.CarDocument;
import fpt.swp391.carrentalsystem.exception.ResourceNotFoundException;
import fpt.swp391.carrentalsystem.repository.CarDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarDocumentServiceImpl implements CarDocumentService {

    private final CarDocumentRepository carDocumentRepository;
    private final FileStorageService fileStorageService;

    @Override
    public CarDocumentDTO uploadDocument(
            MultipartFile file,
            Long ownerId,
            String documentType,
            LocalDate expiryDate) {

        // Validate document type
        CarDocument.DocumentType docType;
        try {
            docType = CarDocument.DocumentType.valueOf(documentType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type: " + documentType);
        }

        // Kiểm tra xem đã upload loại này chưa
        carDocumentRepository.findByOwnerIdAndDocumentTypeAndCarIdIsNull(ownerId, docType)
                .ifPresent(existing -> {
                    // Xóa file cũ
                    fileStorageService.deleteFile(existing.getFilePath());
                    // Xóa record cũ
                    carDocumentRepository.delete(existing);
                });

        // Store file
        String filePath = fileStorageService.storeFile(file, ownerId, documentType);

        // Create document record
        CarDocument document = new CarDocument();
        document.setOwnerId(ownerId);
        document.setDocumentType(docType);
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(filePath);
        document.setFileSize(file.getSize());
        document.setFileType(file.getContentType());
        document.setExpiryDate(expiryDate);
        document.setStatus(CarDocument.DocumentStatus.PENDING);

        CarDocument savedDocument = carDocumentRepository.save(document);

        return new CarDocumentDTO(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarDocumentDTO> getOwnerDocuments(Long ownerId) {
        return carDocumentRepository.findByOwnerIdAndCarIdIsNull(ownerId).stream()
                .map(CarDocumentDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDocument(Long documentId, Long ownerId) {
        CarDocument document = carDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        // Kiểm tra quyền sở hữu
        if (!document.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("You don't have permission to delete this document");
        }

        // Xóa file
        fileStorageService.deleteFile(document.getFilePath());

        // Xóa record
        carDocumentRepository.delete(document);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllRequiredDocuments(Long ownerId) {
        return carDocumentRepository.hasAllRequiredDocuments(ownerId);
    }

    @Override
    public void attachDocumentsToCar(Long ownerId, Long carId) {
        List<CarDocument> documents = carDocumentRepository.findByOwnerIdAndCarIdIsNull(ownerId);

        documents.forEach(doc -> {
            doc.setCarId(carId);
        });

        carDocumentRepository.saveAll(documents);
    }
}