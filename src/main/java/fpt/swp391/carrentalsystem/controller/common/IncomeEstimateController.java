package fpt.swp391.carrentalsystem.controller.common;

import fpt.swp391.carrentalsystem.dto.request.IncomeEstimateRequestDTO;
import fpt.swp391.carrentalsystem.dto.response.IncomeEstimateResponseDTO;
import fpt.swp391.carrentalsystem.service.IncomeEstimateService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IncomeEstimateController {

    private final IncomeEstimateService incomeEstimateService;

    @PostMapping("/income-estimate")
    public ResponseEntity<ApiResponse<IncomeEstimateResponseDTO>> estimate(
            @Valid @RequestBody IncomeEstimateRequestDTO request,
            HttpSession session) {

        IncomeEstimateResponseDTO estimate =
                incomeEstimateService.calculateIncome(request);

        // 🔥 CHỐT: LƯU SESSION
        session.setAttribute("INCOME_ESTIMATE", estimate);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", estimate)
        );
    }

    // 🔥 API DUY NHẤT ĐỂ STEP1 ĐỌC
    @GetMapping("/income-estimate/session")
    public ResponseEntity<ApiResponse<IncomeEstimateResponseDTO>> getSession(
            HttpSession session) {

        IncomeEstimateResponseDTO estimate =
                (IncomeEstimateResponseDTO) session.getAttribute("INCOME_ESTIMATE");

        if (estimate == null) {
            return ResponseEntity.ok(
                    new ApiResponse<>(false, "NO_ESTIMATE", null)
            );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", estimate)
        );
    }

    public static class ApiResponse<T> {
        public boolean success;
        public String message;
        public T data;
        public ApiResponse(boolean s, String m, T d) {
            success = s; message = m; data = d;
        }
    }
}

