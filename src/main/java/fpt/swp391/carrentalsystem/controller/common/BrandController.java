package fpt.swp391.carrentalsystem.controller.common;



import fpt.swp391.carrentalsystem.dto.request.BrandDTO;
import fpt.swp391.carrentalsystem.dto.request.CarModelDTO;
import fpt.swp391.carrentalsystem.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BrandController {

    private final BrandService brandService;

    /**
     * API lấy tất cả hãng xe
     * GET /api/brands
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandDTO>>> getAllBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        ApiResponse<List<BrandDTO>> response = new ApiResponse<>(
                true,
                "Lấy danh sách hãng xe thành công",
                brands
        );
        return ResponseEntity.ok(response);
    }

    /**
     * API lấy mẫu xe theo hãng
     * GET /api/brands/{brandId}/models
     */
    @GetMapping("/{brandId}/models")
    public ResponseEntity<ApiResponse<List<CarModelDTO>>> getModelsByBrandId(@PathVariable Long brandId) {
        List<CarModelDTO> models = brandService.getModelsByBrandId(brandId);
        ApiResponse<List<CarModelDTO>> response = new ApiResponse<>(
                true,
                "Lấy danh sách mẫu xe thành công",
                models
        );
        return ResponseEntity.ok(response);
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

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}


