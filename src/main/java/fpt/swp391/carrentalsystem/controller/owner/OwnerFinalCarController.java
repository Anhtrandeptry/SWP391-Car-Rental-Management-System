package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.request.FinalCarSubmitDTO;
import fpt.swp391.carrentalsystem.dto.response.CarResponseDto;
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
     * POST /api/owner/cars/create-complete
     */
    @PostMapping("/create-complete")
    public ResponseEntity<ApiResponse<CarResponseDto>> createCompleteCar(
            @Valid @RequestBody FinalCarSubmitDTO submitDTO) {

        // TODO: Sau này lấy ownerId từ JWT
        submitDTO.setOwnerId(1L); // Hardcode tạm

        CarResponseDto createdCar = finalCarCreationService.createCompleteCar(submitDTO);

        ApiResponse<CarResponseDto> response =
                new ApiResponse<>(true,
                        "Tạo xe thành công! Đang chờ admin phê duyệt.",
                        createdCar);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Response wrapper chung
     */
    public static class ApiResponse<T> {

        private boolean success;
        private String message;
        private T data;

        public ApiResponse() {
        }

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}