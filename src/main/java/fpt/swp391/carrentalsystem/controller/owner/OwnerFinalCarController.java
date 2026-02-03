package fpt.swp391.carrentalsystem.controller.owner;


import fpt.swp391.carrentalsystem.dto.request.CarDocumentDTO;
import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.service.FinalCarCreationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller submit cuối cùng tạo xe
 */
@RestController
@RequestMapping("/api/owner/cars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerFinalCarController {

    private final FinalCarCreationService finalCarCreationService;

    /**
     * API submit cuối cùng để tạo xe hoàn chỉnh
     * POST /api/owner/cars/create-complete
     * Access: Owner only
     */
    @PostMapping("/create-complete")
    public ResponseEntity<ApiResponse<CarDocumentDTO>> createCompleteCar(
            @Valid @RequestBody FinalCarSubmitDTO submitDTO) {

        // TODO: Get ownerId from JWT token and set vào submitDTO
        submitDTO.setOwnerId(1L); // Hardcode tạm

        try {
            CarDocumentDTO createdCar = finalCarCreationService.createCompleteCar(submitDTO);

            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Tạo xe thành công! Đang chờ admin phê duyệt.", createdCar),
                    HttpStatus.CREATED
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
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
