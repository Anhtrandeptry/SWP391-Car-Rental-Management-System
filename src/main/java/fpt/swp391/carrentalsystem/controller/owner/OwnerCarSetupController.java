package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.request.CarSetupDTO;
import fpt.swp391.carrentalsystem.service.CarValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý Step 1: Thiết lập xe
 */
@RestController
@RequestMapping("/api/owner/car-setup")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerCarSetupController {

    private final CarValidationService carValidationService;

    /**
     * API kiểm tra biển số xe có tồn tại không
     * GET /api/owner/car-setup/check-license-plate?licensePlate=30A-12345
     * Access: Owner only
     */
    @GetMapping("/check-license-plate")
    public ResponseEntity<ApiResponse<Boolean>> checkLicensePlate(
            @RequestParam String licensePlate) {

        boolean exists = carValidationService.isLicensePlateExists(licensePlate);

        if (exists) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, "Biển số xe đã tồn tại trong hệ thống", exists)
            );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Biển số xe có thể sử dụng", exists)
        );
    }

    /**
     * API validate dữ liệu Step 1
     * POST /api/owner/car-setup/validate-step1
     * Access: Owner only
     */
    @PostMapping("/api/income-estimate")
    public ResponseEntity<ApiResponse<Map<String, String>>> validateStep1(
            @Valid @RequestBody CarSetupDTO carSetupDTO,
            BindingResult bindingResult) {

        // Kiểm tra validation errors từ @Valid
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Dữ liệu không hợp lệ", errors)
            );
        }

        // Kiểm tra business logic validation
        try {
            carValidationService.validateStep1(carSetupDTO);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Dữ liệu hợp lệ", null)
            );
        } catch (IllegalArgumentException e) {
            Map<String, String> errors = new HashMap<>();
            errors.put("general", e.getMessage());

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), errors)
            );
        }
    }

    /**
     * API lưu tạm dữ liệu Step 1 (Optional - nếu muốn lưu draft)
     * POST /api/owner/car-setup/save-draft
     * Access: Owner only
     */
    @PostMapping("/save-draft")
    public ResponseEntity<ApiResponse<CarSetupDTO>> saveDraft(
            @Valid @RequestBody CarSetupDTO carSetupDTO) {

        // TODO: Implement save draft logic
        // Có thể lưu vào Redis hoặc database với trạng thái DRAFT

        return new ResponseEntity<>(
                new ApiResponse<>(true, "Lưu nháp thành công", carSetupDTO),
                HttpStatus.CREATED
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

