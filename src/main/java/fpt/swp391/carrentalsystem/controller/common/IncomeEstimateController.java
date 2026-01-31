package fpt.swp391.carrentalsystem.controller.common;

import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateRequestDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;
import fpt.swp391.carrentalsystem.service.IncomeEstimateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/income-estimate")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IncomeEstimateController {

    private final IncomeEstimateService incomeEstimateService;

    /**
     * API ước tính thu nhập
     * POST /api/income-estimate
     */
    @PostMapping
    public ResponseEntity<ApiResponse<IncomeEstimateResponseDTO>> estimateIncome(
            @Valid @RequestBody IncomeEstimateRequestDTO request) {

        IncomeEstimateResponseDTO estimate = incomeEstimateService.calculateIncome(request);
        ApiResponse<IncomeEstimateResponseDTO> response = new ApiResponse<>(
                true,
                "Tính toán thu nhập ước tính thành công",
                estimate
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
//package fpt.swp391.carrentalsystem.controller.common;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class IncomeEstimateController {
//
//    @GetMapping("/income-estimate")
//    public String showIncomeEstimatePage() {
//        return "public/income-estimate";
//    }
//}


