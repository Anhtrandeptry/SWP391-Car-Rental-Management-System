//package fpt.swp391.carrentalsystem.controller.owner;
////import fpt.swp391.carrentalsystem.dto.request.CarRequestDTO;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//

///**
// * Controller quản lý xe cho Owner (Chủ xe)
// */
//@RestController
//@RequestMapping("/api/owner/cars")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
//public class OwnerCarController {
//
//    private final CarService carService;
//
//    /**
//     * API tạo xe mới
//     * POST /api/owner/cars
//     * Access: Owner only
//     */
//    @PostMapping
//    public ResponseEntity<ApiResponse<CarResponseDTO>> createCar(
//            @Valid @RequestBody CarRequestDTO carRequestDTO) {
//
//        // TODO: Lấy ownerId từ JWT token sau khi implement authentication
//        // Long ownerId = getCurrentUserId();
//        // carRequestDTO.setOwnerId(ownerId);
//
//        CarResponseDTO createdCar = carService.createCar(carRequestDTO);
//        return new ResponseEntity<>(
//                new ApiResponse<>(true, "Tạo xe thành công", createdCar),
//                HttpStatus.CREATED
//        );
//    }
//
//    /**
//     * API lấy danh sách xe của owner đang đăng nhập
//     * GET /api/owner/cars
//     * Access: Owner only
//     */
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> getMyCars() {
//        // TODO: Lấy ownerId từ JWT token
//        Long ownerId = 1L; // Hardcode tạm, sau sẽ lấy từ JWT
//
//        List<CarResponseDTO> cars = carService.getCarsByOwnerId(ownerId);
//        return ResponseEntity.ok(
//                new ApiResponse<>(true, "Lấy danh sách xe của bạn thành công", cars)
//        );
//    }
//
//    /**
//     * API lấy thông tin xe theo ID (chỉ xe của owner)
//     * GET /api/owner/cars/{id}
//     * Access: Owner only
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<CarResponseDTO>> getMyCarById(@PathVariable Long id) {
//        // TODO: Kiểm tra xe có thuộc owner đang đăng nhập không
//
//        CarResponseDTO car = carService.getCarById(id);
//        return ResponseEntity.ok(
//                new ApiResponse<>(true, "Lấy thông tin xe thành công", car)
//        );
//    }
//
//    /**
//     * API cập nhật thông tin xe
//     * PUT /api/owner/cars/{id}
//     * Access: Owner only (chỉ update xe của mình)
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<CarResponseDTO>> updateMyCar(
//            @PathVariable Long id,
//            @Valid @RequestBody CarRequestDTO carRequestDTO) {
//
//        // TODO: Kiểm tra xe có thuộc owner đang đăng nhập không
//
//        CarResponseDTO updatedCar = carService.updateCar(id, carRequestDTO);
//        return ResponseEntity.ok(
//                new ApiResponse<>(true, "Cập nhật xe thành công", updatedCar)
//        );
//    }
//
//    /**
//     * API xóa xe
//     * DELETE /api/owner/cars/{id}
//     * Access: Owner only (chỉ delete xe của mình)
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Void>> deleteMyCar(@PathVariable Long id) {
//        // TODO: Kiểm tra xe có thuộc owner đang đăng nhập không
//
//        carService.deleteCar(id);
//        return ResponseEntity.ok(
//                new ApiResponse<>(true, "Xóa xe thành công", null)
//        );
//    }
//
//    /**
//     * API tính toán lại thu nhập ước tính cho xe
//     * POST /api/owner/cars/{id}/calculate-income
//     * Access: Owner only
//     */
//    @PostMapping("/{id}/calculate-income")
//    public ResponseEntity<ApiResponse<CarResponseDTO>> calculateMyCarIncome(@PathVariable Long id) {
//        // TODO: Kiểm tra xe có thuộc owner đang đăng nhập không
//
//        CarResponseDTO car = carService.calculateEstimatedIncome(id);
//        return ResponseEntity.ok(
//                new ApiResponse<>(true, "Tính toán thu nhập ước tính thành công", car)
//        );
//    }
//
//    // Inner class cho API Response
//    public static class ApiResponse<T> {
//        private boolean success;
//        private String message;
//        private T data;
//
//        public ApiResponse(boolean success, String message, T data) {
//            this.success = success;
//            this.message = message;
//            this.data = data;
//        }
//
//        // Getters and Setters
//        public boolean isSuccess() { return success; }
//        public void setSuccess(boolean success) { this.success = success; }
//        public String getMessage() { return message; }
//        public void setMessage(String message) { this.message = message; }
//        public T getData() { return data; }
//        public void setData(T data) { this.data = data; }
//    }
//}


