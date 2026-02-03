package fpt.swp391.carrentalsystem.controller.owner;



import fpt.swp391.carrentalsystem.dto.request.CarDocumentDTO;
import fpt.swp391.carrentalsystem.service.CarDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller xử lý Step 3: Upload giấy tờ
 */
@RestController
@RequestMapping("/api/owner/car-documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerCarDocumentController {

    private final CarDocumentService carDocumentService;

    /**
     * API upload document
     * POST /api/owner/car-documents/upload
     * Access: Owner only
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<CarDocumentDTO>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "expiryDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {

        // TODO: Get ownerId from JWT token
        Long ownerId = 1L; // Hardcode tạm

        try {
            CarDocumentDTO document = carDocumentService.uploadDocument(
                    file,
                    ownerId,
                    documentType,
                    expiryDate
            );

            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Upload giấy tờ thành công", document),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    /**
     * API lấy danh sách documents đã upload
     * GET /api/owner/car-documents
     * Access: Owner only
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CarDocumentDTO>>> getDocuments() {
        // TODO: Get ownerId from JWT token
        Long ownerId = 1L;

        List<CarDocumentDTO> documents = carDocumentService.getOwnerDocuments(ownerId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy danh sách giấy tờ thành công", documents)
        );
    }

    /**
     * API xóa document
     * DELETE /api/owner/car-documents/{id}
     * Access: Owner only
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        // TODO: Get ownerId from JWT token
        Long ownerId = 1L;

        try {
            carDocumentService.deleteDocument(id, ownerId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Xóa giấy tờ thành công", null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    /**
     * API kiểm tra đã upload đủ giấy tờ bắt buộc chưa
     * GET /api/owner/car-documents/check-required
     * Access: Owner only
     */
    @GetMapping("/check-required")
    public ResponseEntity<ApiResponse<Boolean>> checkRequiredDocuments() {
        // TODO: Get ownerId from JWT token
        Long ownerId = 1L;

        boolean hasAll = carDocumentService.hasAllRequiredDocuments(ownerId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        hasAll,
                        hasAll ? "Đã upload đủ giấy tờ bắt buộc" : "Chưa đủ giấy tờ bắt buộc",
                        hasAll
                )
        );
    }

    // Inner class cho API Response
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}