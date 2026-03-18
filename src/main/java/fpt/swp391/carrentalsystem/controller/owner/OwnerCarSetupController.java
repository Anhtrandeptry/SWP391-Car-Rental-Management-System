package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.dto.request.CarSetupDTO;
import fpt.swp391.carrentalsystem.service.CarValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/create-car-step1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OwnerCarSetupController {

    private final CarValidationService carValidationService;

    /* ================= CHECK LICENSE PLATE ================= */
    @GetMapping("/check-license-plate")
    public ResponseEntity<ApiResponse<Boolean>> checkLicensePlate(
            @RequestParam String licensePlate
    ) {
        // Normalize: trim, uppercase, remove all whitespace
        String normalizedPlate = licensePlate.trim().toUpperCase().replaceAll("\\s+", "");

        if (normalizedPlate.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, "Biển số xe không được để trống", false)
            );
        }

        boolean exists = carValidationService.isLicensePlateExists(normalizedPlate);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        !exists,
                        exists ? "Biển số xe đã tồn tại, vui lòng nhập biển số khác" : "Biển số hợp lệ",
                        exists
                )
        );
    }

    /* ================= VALIDATE STEP 1 ================= */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, String>>> validateStep1(
            @Valid @RequestBody CarSetupDTO dto,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError e : bindingResult.getFieldErrors()) {
                errors.put(e.getField(), e.getDefaultMessage());
            }

            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Dữ liệu không hợp lệ", errors));
        }

        try {
            carValidationService.validateStep1(dto);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Dữ liệu hợp lệ", null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            Map.of("general", e.getMessage())
                    ));
        }
    }

    /* ================= API RESPONSE ================= */
    public record ApiResponse<T>(
            boolean success,
            String message,
            T data
    ) {}
}
