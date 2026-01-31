package fpt.swp391.carrentalsystem.controller.owner;


import fpt.swp391.carrentalsystem.dto.request.CarInfoDTO;
import fpt.swp391.carrentalsystem.service.CarInfoService;
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
 * Controller xử lý Step 2: Thông tin chi tiết xe
 */
@RestController
@RequestMapping("/api/owner/car-info")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerCarInfoController {

    private final CarInfoService carInfoService;

    /**
     * API validate dữ liệu Step 2
     * POST /api/owner/car-info/validate-step2
     * Access: Owner only
     */
    @PostMapping("/validate-step2")
    public ResponseEntity<ApiResponse<Map<String, String>>> validateStep2(
            @Valid @RequestBody CarInfoDTO carInfoDTO,
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
            carInfoService.validateStep2(carInfoDTO);

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
     * API lưu nháp Step 2
     * POST /api/owner/car-info/save-draft
     * Access: Owner only
     */
    @PostMapping("/save-draft")
    public ResponseEntity<ApiResponse<CarInfoDTO>> saveDraft(
            @Valid @RequestBody CarInfoDTO carInfoDTO) {

        CarInfoDTO savedData = carInfoService.saveDraftStep2(carInfoDTO);

        return new ResponseEntity<>(
                new ApiResponse<>(true, "Lưu nháp thành công", savedData),
                HttpStatus.CREATED
        );
    }

    /**
     * API lấy nháp Step 2
     * GET /api/owner/car-info/draft
     * Access: Owner only
     */
    @GetMapping("/draft")
    public ResponseEntity<ApiResponse<CarInfoDTO>> getDraft() {
        // TODO: Get ownerId from JWT token
        Long ownerId = 1L;

        CarInfoDTO draftData = carInfoService.getDraftStep2(ownerId);

        if (draftData == null) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, "Không tìm thấy nháp", null)
            );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Lấy nháp thành công", draftData)
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