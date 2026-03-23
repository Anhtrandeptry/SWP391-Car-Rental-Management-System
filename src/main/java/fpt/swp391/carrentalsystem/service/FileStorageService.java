package fpt.swp391.carrentalsystem.service;


import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {

    /**
     * Lưu file upload
     */
    String storeFile(MultipartFile file, Long ownerId, String documentType);

    /**
     * Xóa file
     */
    void deleteFile(String filePath);

    /**
     * Lấy đường dẫn file
     */
    Path loadFile(String filename);

    /**
     * Validate file
     */
    void validateFile(MultipartFile file);
}
